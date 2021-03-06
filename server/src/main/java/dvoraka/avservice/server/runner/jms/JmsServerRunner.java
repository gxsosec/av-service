package dvoraka.avservice.server.runner.jms;

import dvoraka.avservice.common.runner.AbstractServiceRunner;
import dvoraka.avservice.common.service.ServiceManagement;
import dvoraka.avservice.server.BasicAvServer;
import dvoraka.avservice.server.configuration.jms.JmsConfig;

import java.io.IOException;

/**
 * JMS server runner.
 */
public class JmsServerRunner extends AbstractServiceRunner {

    public static void main(String[] args) throws IOException {
        JmsServerRunner runner = new JmsServerRunner();
        runner.run();
    }

    @Override
    public String[] profiles() {
        return new String[]{"core", "client", "jms", "jms-server", "db"};
    }

    @Override
    public Class<?>[] configClasses() {
        return new Class<?>[]{JmsConfig.class};
    }

    @Override
    public Class<? extends ServiceManagement> runClass() {
        return BasicAvServer.class;
    }
}
