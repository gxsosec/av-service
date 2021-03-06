package dvoraka.avservice.server.runner.amqp;

import dvoraka.avservice.common.runner.AbstractServiceRunner;
import dvoraka.avservice.common.runner.ServiceRunner;
import dvoraka.avservice.common.service.ServiceManagement;
import dvoraka.avservice.server.ServerComponentBridge;
import dvoraka.avservice.server.configuration.bridge.AmqpToJmsConfig;

import java.io.IOException;

/**
 * AMQP to JMS bridge runner.
 */
public class AmqpToJmsBridgeRunner extends AbstractServiceRunner {

    public static void main(String[] args) throws IOException {
        ServiceRunner runner = new AmqpToJmsBridgeRunner();
        runner.run();
    }

    @Override
    public String[] profiles() {
        return new String[]{"core", "amqp2jms", "db"};
    }

    @Override
    public Class<?>[] configClasses() {
        return new Class<?>[]{AmqpToJmsConfig.class};
    }

    @Override
    public Class<? extends ServiceManagement> runClass() {
        return ServerComponentBridge.class;
    }
}
