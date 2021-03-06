package dvoraka.avservice.server.configuration.amqp;

import dvoraka.avservice.client.ServerComponent;
import dvoraka.avservice.client.amqp.AmqpComponent;
import dvoraka.avservice.common.amqp.AvMessageConverter;
import dvoraka.avservice.common.amqp.AvMessageMapper;
import dvoraka.avservice.db.service.MessageInfoService;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AMQP bridge output configuration for import.
 */
@Configuration
public class AmqpBridgeOutputConfig {

    @Value("${avservice.amqp.host:localhost}")
    private String host;
    @Value("${avservice.amqp.vhost:antivirus}")
    private String virtualHost;

    @Value("${avservice.amqp.resultQueue:av-result}")
    private String resultQueue;
    @Value("${avservice.amqp.checkExchange:check}")
    private String checkExchange;

    @Value("${avservice.amqp.listeningTimeout:4000}")
    private long listeningTimeout;

    @Value("${avservice.amqp.user:guest}")
    private String userName;
    @Value("${avservice.amqp.pass:guest}")
    private String userPassword;

    @Value("${avservice.serviceId:default1}")
    private String serviceId;


    @Bean
    public ServerComponent outComponent(
            RabbitTemplate outRabbitTemplate,
            MessageInfoService messageInfoService
    ) {
        return new AmqpComponent(checkExchange, serviceId, outRabbitTemplate, messageInfoService);
    }

    @Bean
    public ConnectionFactory outConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host);
        connectionFactory.setUsername(userName);
        connectionFactory.setPassword(userPassword);
        connectionFactory.setVirtualHost(virtualHost);

        return connectionFactory;
    }

    @Bean
    public AmqpAdmin outAmqpAdmin(ConnectionFactory outConnectionFactory) {
        return new RabbitAdmin(outConnectionFactory);
    }

    @Bean
    public AvMessageMapper avMessageMapper() {
        return new AvMessageMapper();
    }

    @Bean
    public MessageConverter outMessageConverter(AvMessageMapper messageMapper) {
        return new AvMessageConverter(messageMapper);
    }

    @Bean
    public RabbitTemplate outRabbitTemplate(
            ConnectionFactory outConnectionFactory,
            MessageConverter outMessageConverter
    ) {
        RabbitTemplate template = new RabbitTemplate(outConnectionFactory);
        template.setReceiveTimeout(listeningTimeout);
        template.setQueue(resultQueue);
        template.setMessageConverter(outMessageConverter);

        return template;
    }

    @Bean
    public MessageListener outMessageListener(ServerComponent outComponent) {
        return outComponent;
    }

    @Bean
    public MessageListenerContainer outMessageListenerContainer(
            ConnectionFactory outConnectionFactory,
            MessageListener outMessageListener
    ) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(outConnectionFactory);
        container.setQueueNames(resultQueue);
        container.setMessageListener(outMessageListener);

        return container;
    }
}
