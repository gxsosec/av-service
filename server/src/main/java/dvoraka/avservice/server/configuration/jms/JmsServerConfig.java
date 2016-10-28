package dvoraka.avservice.server.configuration.jms;

import dvoraka.avservice.server.AvServer;
import dvoraka.avservice.server.BasicAvServer;
import dvoraka.avservice.server.ServerComponent;
import dvoraka.avservice.server.jms.JmsComponent;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.listener.SimpleMessageListenerContainer;

import javax.jms.MessageListener;

/**
 * JMS server configuration for import.
 */
@Configuration
@Profile("jms-server")
public class JmsServerConfig {

    @Value("${avservice.jms.resultDestination:result}")
    private String resultDestination;

    @Value("${avservice.jms.checkDestination:check}")
    private String checkDestination;

    @Value("${avservice.serviceId:default1}")
    private String serviceId;


    @Bean
    public AvServer avServer() {
        return new BasicAvServer(serviceId);
    }

    @Bean
    public ServerComponent serverComponent() {
        return new JmsComponent(resultDestination, serviceId);
    }

    @Bean
    public MessageListener messageListener() {
        return serverComponent();
    }

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(
            ActiveMQConnectionFactory activeMQConnectionFactory,
            MessageListener messageListener) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(activeMQConnectionFactory);
        container.setDestinationName(checkDestination);
        container.setMessageListener(messageListener);

        return container;
    }
}