package dvoraka.avservice.server.configuration

import dvoraka.avservice.server.AvServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Configuration test.
 */
@ContextConfiguration(classes = [JmsConfig.class])
@ActiveProfiles(["jms", "jms-async", "no-db"])
class JmsConfigISpec extends Specification {

    @Autowired
    AvServer server


    def "test"() {
        expect:
            true
    }
}
