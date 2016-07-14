package dvoraka.avservice.server.amqp;

import dvoraka.avservice.MessageProcessor;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.AvMessageMapper;
import dvoraka.avservice.common.exception.MapperException;
import dvoraka.avservice.server.ListeningStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Parallel AMQP strategy prototype for messages receiving.
 */
public class ParallelAmqpListeningStrategy implements ListeningStrategy {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private MessageProcessor messageProcessor;

    private static final Logger log = LogManager.getLogger(ParallelAmqpListeningStrategy.class.getName());

    private boolean running;
    private int listeners;
    private ExecutorService executorService;


    public ParallelAmqpListeningStrategy(int listeners) {
        this.listeners = listeners;
        executorService = Executors.newFixedThreadPool(listeners);
    }

    @Override
    public void listen() {
        log.debug("Listening...");
        setRunning(true);

        for (int i = 0; i < listeners; i++) {
            executorService.execute(this::receiveAndProcess);
        }
    }

    private void receiveAndProcess() {
        while (isRunning()) {

            log.debug("Waiting for a message...");
            Message message = rabbitTemplate.receive();

            if (message != null) {
                log.debug("Message received.");
                try {
                    AvMessage avMessage = AvMessageMapper.transform(message);
                    messageProcessor.sendMessage(avMessage);
                    log.debug("Message sent.");
                } catch (MapperException e) {
                    log.warn("Message problem!", e);
                }
            }
        }

        log.debug("Listening stopped.");
    }

    @Override
    public void stop() {
        log.debug("Stop listening.");
        setRunning(false);
        executorService.shutdown();

        final long waitTime = 10;
        try {
            executorService.awaitTermination(waitTime, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.warn("Stopping interrupted!", e);
            Thread.currentThread().interrupt();
        }
    }

    public boolean isRunning() {
        return running;
    }

    private void setRunning(boolean running) {
        this.running = running;
    }

    public int getListenersCount() {
        return listeners;
    }

    protected void setRabbitTemplate(RabbitTemplate template) {
        rabbitTemplate = template;
    }

    protected void setMessageProcessor(MessageProcessor processor) {
        messageProcessor = processor;
    }
}
