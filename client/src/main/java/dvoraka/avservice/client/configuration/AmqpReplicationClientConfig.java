package dvoraka.avservice.client.configuration;

import dvoraka.avservice.client.ReplicationComponent;
import dvoraka.avservice.client.ServerComponent;
import dvoraka.avservice.client.amqp.AmqpReplicationComponent;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * AMQP client configuration for the import.
 */
@Configuration
@Profile({"amqp-client", "replication"})
public class AmqpReplicationClientConfig {

    @Value("${avservice.amqp.resultQueue}")
    private String resultQueue;
    @Value("${avservice.amqp.checkExchange}")
    private String checkExchange;
    @Value("${avservice.amqp.fileExchange}")
    private String fileExchange;

    @Value("${avservice.serviceId}")
    private String serviceId;


//    @Bean
//    public ServerComponent serverComponent(
//            RabbitTemplate rabbitTemplate,
//            MessageInfoService messageInfoService
//    ) {
//        return new AmqpComponent(fileExchange, serviceId, rabbitTemplate, messageInfoService);
//    }

    @Bean
    public ReplicationComponent replicationComponent(RabbitTemplate rabbitTemplate) {
        return new AmqpReplicationComponent(rabbitTemplate);
    }

    @Bean
    public MessageListener messageListener(ServerComponent serverComponent) {
        return serverComponent;
    }

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(
            ConnectionFactory connectionFactory, MessageListener messageListener) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(resultQueue);
        container.setMessageListener(messageListener);

        return container;
    }
}
