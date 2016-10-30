package dvoraka.avservice.server.configuration.jms

import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Configuration test.
 */
@ContextConfiguration(classes = [JmsConfig.class])
@ActiveProfiles(["jms", "no-db"])
class JmsConfigISpec extends Specification {

    def "test"() {
        expect:
            true
    }
}
