package dvoraka.avservice.server.runner.amqp

import spock.lang.Specification

/**
 * Test for bridge running.
 */
class AmqpToJmsBridgeRunnerISpec extends Specification {

    def "Run AMQP to JMS bridge"() {
        when:
            AmqpToJmsBridgeRunner.setTestRun(true)
            AmqpToJmsBridgeRunner.main([] as String[])

        then:
            notThrown(Exception)
    }
}
