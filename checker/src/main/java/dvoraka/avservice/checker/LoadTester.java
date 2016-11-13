package dvoraka.avservice.checker;

import dvoraka.avservice.checker.configuration.LoadTestConfig;
import dvoraka.avservice.checker.receiver.AvReceiver;
import dvoraka.avservice.checker.sender.AvSender;
import dvoraka.avservice.common.BasicLoadTestProperties;
import dvoraka.avservice.common.LoadTestProperties;
import dvoraka.avservice.common.exception.LastMessageException;
import dvoraka.avservice.common.exception.MaxLoopsReachedException;
import dvoraka.avservice.common.exception.ProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * AMQP anti-virus load tester.
 */
public class LoadTester implements Tester {

    @Autowired
    private AvSender avSender;
    @Autowired
    private AvReceiver avReceiver;

    private static final Logger log = LogManager.getLogger();

    private static final int MAX_MSG_EXCEPTIONS = 3;
    private static final int MAX_LOOPS = 10;
    private static final float MS_PER_SECOND = 1000f;

    private LoadTestProperties props;

    private int maxLoops;
    private int maxMsgExceptions;


    public LoadTester(LoadTestProperties props) {
        if (props == null) {
            this.props = new BasicLoadTestProperties();
        } else {
            this.props = props;
        }

        this.maxLoops = MAX_LOOPS;
        this.maxMsgExceptions = MAX_MSG_EXCEPTIONS;
    }

    public static void main(String[] args) throws IOException {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(LoadTestConfig.class);
        Tester lt = context.getBean(LoadTester.class);
        lt.startTest();

        context.close();
    }

    public Collection<String> sendMessages() throws IOException {
        Collection<String> messageIDs = new HashSet<>(getProps().getMsgCount() / 2);

        for (int i = 0; i < getProps().getMsgCount(); i++) {
            messageIDs.add(getAvSender().sendFile(true, getProps().getAppId()));
        }

        return messageIDs;
    }

    public void receiveMessages(Collection<String> messageIDs)
            throws IOException, MaxLoopsReachedException {

        int loopCounter = 1;
        while (!messageIDs.isEmpty()) {
            if (loopCounter > maxLoops) {
                throw new MaxLoopsReachedException();
            }

            int exceptionCounter = 0;
            Iterator<String> it = messageIDs.iterator();
            while (it.hasNext()) {
                try {
                    String item = it.next();
                    getAvReceiver().receive(item);
                    it.remove();
                } catch (InterruptedException e) {
                    log.warn("Receiving interrupted!");
                    Thread.currentThread().interrupt();
                } catch (ProtocolException e) {
                    log.info(e);
                } catch (LastMessageException e) {
                    log.warn("", e);
                    if (exceptionCounter < this.maxMsgExceptions) {
                        exceptionCounter++;
                    } else {
                        break;
                    }
                }
            }
            log.debug(loopCounter + ". loop");

            try {
                final long sleepTime = 1000;
                TimeUnit.MILLISECONDS.sleep(sleepTime);
            } catch (InterruptedException e) {
                log.warn("Sleeping interrupted!");
                Thread.currentThread().interrupt();
            }

            loopCounter++;
        }
    }

    @Override
    public void startTest() throws IOException {
        System.out.println("Load test start for " + getProps().getMsgCount() + " messages...");
        long begin = System.currentTimeMillis();

        // purge queue before test start
        getAvSender().purgeQueue(getProps().getDestinationQueue());

        if (getProps().isSynchronous()) {
            synchronousTest();
        } else {
            asynchronousTest(begin);
        }

        printTestingTime(begin);
    }

    private void printTestingTime(long begin) {
        long duration = System.currentTimeMillis() - begin;
        float durationSeconds = duration / MS_PER_SECOND;

        String message = "\n"
                + "Load test end\n"
                + "Duration: " + durationSeconds + " s";
        System.out.println(message);

        System.out.println("Messages: " + getProps().getMsgCount() / durationSeconds + "/s");
    }

    private void synchronousTest() throws IOException {
        System.out.println("Sending and receiving...");

        Collection<String> skippedMessages = new HashSet<>();
        String msgId = null;
        for (int i = 0; i < getProps().getMsgCount(); i++) {
            try {
                msgId = getAvSender().sendFile(true, getProps().getAppId());
                getAvReceiver().receive(msgId);
            } catch (InterruptedException e) {
                log.warn("Receiving interrupted!");
                Thread.currentThread().interrupt();
            } catch (ProtocolException e) {
                log.info(e);
            } catch (LastMessageException e) {
                skippedMessages.add(msgId);
                log.debug("receiving failed", e);
            }
        }

        int loopCounter = 0;
        while (!skippedMessages.isEmpty()) {
            if (loopCounter > maxLoops) {
                try {
                    throw new MaxLoopsReachedException();
                } catch (MaxLoopsReachedException e) {
                    log.warn("unreceivable messages", e);
                    break;
                }
            }

            Iterator<String> it = skippedMessages.iterator();
            while (it.hasNext()) {
                try {
                    getAvReceiver().receive(it.next());
                    it.remove();
                } catch (InterruptedException e) {
                    log.warn("Receiving interrupted!");
                    Thread.currentThread().interrupt();
                } catch (ProtocolException e) {
                    log.info(e);
                } catch (LastMessageException e) {
                    log.debug("receiving failed", e);
                }
            }

            loopCounter++;
        }

        System.out.println("Done");
    }

    private void asynchronousTest(long begin) throws IOException {
        System.out.println("Sending messages...");
        Collection<String> messageIDs = sendMessages();

        printSendingTime(begin);

        if (!getProps().isSendOnly()) {
            // receive messages
            System.out.println("Receiving responses...");

            try {
                receiveMessages(messageIDs);
            } catch (MaxLoopsReachedException e) {
                log.warn("receiving messages failed", e);
            }

            System.out.println("Receiving end");
        }
    }

    private void printSendingTime(long begin) {
        long afterSend = System.currentTimeMillis();
        System.out.println("All messages sent");
        System.out.println("Time: " + ((afterSend - begin) / MS_PER_SECOND) + " s");
        System.out.println("");
    }

    public LoadTestProperties getProps() {
        return props;
    }

    public void setProps(LoadTestProperties props) {
        this.props = props;
    }

    public AvSender getAvSender() {
        return avSender;
    }

    public AvReceiver getAvReceiver() {
        return avReceiver;
    }
}
