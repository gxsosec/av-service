package dvoraka.avservice.server;

import dvoraka.avservice.server.configuration.JmsToAmqpConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Server component bridge.
 */
@Component
public class ServerComponentBridge {

    @Autowired
    private ServerComponent inComponent;
    @Autowired
    private ServerComponent outComponent;


    public static void main(String[] args) throws IOException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles("db-solr", "jms2amqp");
        context.register(JmsToAmqpConfig.class);
        context.refresh();

        ServerComponentBridge bridge = context.getBean(ServerComponentBridge.class);
        bridge.start();

        System.out.println("Press Enter to stop the bridge.");
        System.in.read();

        context.close();
    }

    public ServerComponentBridge() {
    }

    public void start() {
        inComponent.addAvMessageListener(message -> outComponent.sendMessage(message));
        outComponent.addAvMessageListener(message -> inComponent.sendMessage(message));
    }
}