package dvoraka.avservice.rest.service;

import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.MessageStatus;
import dvoraka.avservice.common.service.BasicMessageStatusStorage;
import dvoraka.avservice.common.service.MessageStatusStorage;
import dvoraka.avservice.server.ServerComponent;
import dvoraka.avservice.server.client.service.AvServiceClient;
import dvoraka.avservice.server.client.service.FileServiceClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

/**
 * Remote REST service implementation. Receives requests through REST
 * and sends it along over the network.
 */
@Service
public class RemoteRestService implements RestService, AvMessageListener {

    private final ServerComponent serverComponent;
    private final AvServiceClient avServiceClient;
    private final FileServiceClient fileServiceClient;

    private static final Logger log = LogManager.getLogger(RemoteRestService.class);

    private static final String CACHE_NAME = "remoteRestCache";
    public static final int CACHE_TIMEOUT = 10 * 60 * 1_000;

    private final MessageStatusStorage statusStorage;

    private volatile boolean started;

    // message caching
    private CacheManager cacheManager;
    private Cache<String, AvMessage> messageCache;


    @Autowired
    public RemoteRestService(
            ServerComponent serverComponent,
            AvServiceClient avServiceClient,
            FileServiceClient fileServiceClient
    ) {
        this.serverComponent = requireNonNull(serverComponent);
        this.avServiceClient = requireNonNull(avServiceClient);
        this.fileServiceClient = requireNonNull(fileServiceClient);
        statusStorage = new BasicMessageStatusStorage(CACHE_TIMEOUT);
    }

    private void initializeCache() {
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache(CACHE_NAME, getCacheConfiguration())
                .build(true);

        messageCache = cacheManager.getCache(CACHE_NAME, String.class, AvMessage.class);
    }

    private CacheConfiguration<String, AvMessage> getCacheConfiguration() {
        final long expirationTime = 10_000;
        final long heapEntries = 10;

        return CacheConfigurationBuilder
                .newCacheConfigurationBuilder(
                        String.class, AvMessage.class, ResourcePoolsBuilder.heap(heapEntries))
                .withExpiry(Expirations.timeToLiveExpiration(
                        new Duration(expirationTime, TimeUnit.MILLISECONDS)))
                .build();
    }

    @Override
    public MessageStatus messageStatus(String id) {
        return statusStorage.getStatus(id);
    }

    private void addToProcessing(AvMessage message) {
        statusStorage.started(message.getId());
    }

    private void processMessage(AvMessage message) {
        addToProcessing(message);
        serverComponent.sendAvMessage(message);
    }

    @Override
    public void checkMessage(AvMessage message) {
        log.debug("Checking: {}", message);
        addToProcessing(message);
        avServiceClient.checkMessage(message);
    }

    @Override
    public void saveFile(AvMessage message) {
        log.debug("Saving: {}", message);
        addToProcessing(message);
        fileServiceClient.saveFile(message);
    }

    @Override
    public void loadFile(AvMessage message) {
        log.debug("Loading: {}", message);
        processMessage(message);
    }

    @Override
    public void updateFile(AvMessage message) {
        log.debug("Updating: {}", message);
        processMessage(message);
    }

    @Override
    public void deleteFile(AvMessage message) {
        log.debug("Deleting: {}", message);
        processMessage(message);
    }

    @Override
    public AvMessage getResponse(String id) {
        return messageCache.get(id);
    }

    @PostConstruct
    @Override
    public void start() {
        if (!isStarted()) {
            log.info("Starting.");
            setStarted(true);

            initializeCache();
            serverComponent.addAvMessageListener(this);
        } else {
            log.info("Service is already started.");
        }
    }

    @PreDestroy
    @Override
    public void stop() {
        if (isStarted()) {
            log.info("Stopping.");
            setStarted(false);

            serverComponent.removeAvMessageListener(this);

            statusStorage.stop();

            cacheManager.close();
        } else {
            log.info("Service is already stopped.");
        }
    }

    @Override
    public void onAvMessage(AvMessage response) {
        log.debug("REST on message: {}", response);

        // skip other messages
        if (statusStorage.getStatus(response.getCorrelationId()) == MessageStatus.PROCESSING) {
            log.debug("Saving response: {}", response);
            messageCache.put(response.getCorrelationId(), response);
            statusStorage.processed(response.getCorrelationId());
        }
    }

    public boolean isStarted() {
        return started;
    }

    private void setStarted(boolean started) {
        this.started = started;
    }
}
