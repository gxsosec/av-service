package dvoraka.avservice.server;

import dvoraka.avservice.common.Utils;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.AvMessageMapper;
import dvoraka.avservice.common.exception.MapperException;
import dvoraka.avservice.server.configuration.AmqpConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Configure environment.
 */
public final class EnvironmentConfigurator {

    private EnvironmentConfigurator() {
    }

    public static void main(String[] args) throws MapperException {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles("amqp");
        context.register(AmqpConfig.class);
        context.refresh();

        AvMessage avMessage = Utils.genNormalMessage();
        Message message = AvMessageMapper.transform(avMessage);

        RabbitTemplate rabbitTemplate = context.getBean(RabbitTemplate.class);
        rabbitTemplate.send(message);

        context.close();
    }
}
