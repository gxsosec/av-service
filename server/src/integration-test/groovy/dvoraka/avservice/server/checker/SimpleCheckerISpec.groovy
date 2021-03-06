package dvoraka.avservice.server.checker

import dvoraka.avservice.client.checker.Checker
import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.exception.MessageNotFoundException
import dvoraka.avservice.common.runner.ServiceRunner
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

/**
 * Simple checker spec base.
 */
@Ignore
class SimpleCheckerISpec extends Specification {

    @Autowired
    Checker checker

    @Shared
    ServiceRunner runner


    def setupSpec() {
        // initialize and start runner
    }

    def cleanupSpec() {
        runner.stop()
    }

    def setup() {
        while (!runner.isRunning()) {
            sleep(200)
        }
    }

    def "send and receive normal message"() {
        given:
            AvMessage message = Utils.genMessage()

        when:
            checker.sendAvMessage(message)
            AvMessage receivedMessage = checker.receiveMessage(message.getId())

        then:
            notThrown(Exception)
            receivedMessage.virusInfo == Utils.OK_VIRUS_INFO
    }

    def "send and receive infected message"() {
        given:
            AvMessage message = Utils.genInfectedMessage()

        when:
            checker.sendAvMessage(message)
            AvMessage receivedMessage = checker.receiveMessage(message.getId())

        then:
            notThrown(Exception)
            receivedMessage.virusInfo != Utils.OK_VIRUS_INFO
    }

    def "send few normal messages after infected one and receive the first one"() {
        given:
            int count = 5
            AvMessage message = Utils.genInfectedMessage()

        when:
            checker.sendAvMessage(message)
            count.times {
                checker.sendAvMessage(Utils.genMessage())
            }
            AvMessage resultMessage = checker.receiveMessage(message.getId())

        then:
            resultMessage.getVirusInfo() != Utils.OK_VIRUS_INFO
    }

    def "try to receive nonexistent message"() {
        when:
            checker.receiveMessage("XXX")

        then:
            thrown(MessageNotFoundException)
    }

    def "check method testing"() {
        expect:
            checker.check()
    }
}
