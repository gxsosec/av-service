package dvoraka.avservice.rest.service;

import dvoraka.avservice.MessageProcessor;
import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.MessageStatus;
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
 * Local REST service implementation. Uses directly the message processors.
 */
@Service
public class LocalRestService implements RestService, AvMessageListener {

    private final MessageProcessor checkAndFileProcessor;

    private static final Logger log = LogManager.getLogger(LocalRestService.class);

    private final CacheManager cacheManager;
    private final Cache<String, AvMessage> messageCache;


    @Autowired
    public LocalRestService(MessageProcessor checkAndFileProcessor) {
        this.checkAndFileProcessor = requireNonNull(checkAndFileProcessor);

        cacheManager = buildManager(cacheConfiguration());
        messageCache = cacheManager.getCache("restCache", String.class, AvMessage.class);
    }

    private CacheConfiguration<String, AvMessage> cacheConfiguration() {
        final long expirationTime = 10_000;
        final long heapEntries = 10;

        return CacheConfigurationBuilder
                .newCacheConfigurationBuilder(
                        String.class, AvMessage.class, ResourcePoolsBuilder.heap(heapEntries))
                .withExpiry(Expirations.timeToLiveExpiration(
                        new Duration(expirationTime, TimeUnit.MILLISECONDS)))
                .build();
    }


    private CacheManager buildManager(CacheConfiguration<String, AvMessage> configuration) {
        return CacheManagerBuilder.newCacheManagerBuilder()
                .withCache("restCache", configuration)
                .build(true);
    }

    @Override
    public MessageStatus messageStatus(String id) {
        return checkAndFileProcessor.messageStatus(id);
    }

    @Override
    public void checkMessage(AvMessage message) {
        checkAndFileProcessor.sendMessage(message);
    }

    @Override
    public void saveFile(AvMessage message) {
        checkAndFileProcessor.sendMessage(message);
    }

    @Override
    public void loadFile(AvMessage message) {
        checkAndFileProcessor.sendMessage(message);
    }

    @Override
    public void updateFile(AvMessage message) {
        checkAndFileProcessor.sendMessage(message);
    }

    @Override
    public void deleteFile(AvMessage message) {
        checkAndFileProcessor.sendMessage(message);
    }

    @Override
    public AvMessage getResponse(String id) {
        return messageCache.get(id);
    }

    @PostConstruct
    @Override
    public void start() {
        log.debug("Starting cache updating...");
        checkAndFileProcessor.addProcessedAVMessageListener(this);
    }

    @Override
    @PreDestroy
    public void stop() {
        cacheManager.close();
        checkAndFileProcessor.stop();
    }

    @Override
    public void onAvMessage(AvMessage message) {
        messageCache.put(message.getCorrelationId(), message);
        log.debug("Saving message: " + message.getId());
    }
}
