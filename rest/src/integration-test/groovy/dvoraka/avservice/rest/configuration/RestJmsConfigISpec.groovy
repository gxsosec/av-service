package dvoraka.avservice.rest.configuration

import dvoraka.avservice.rest.RestStrategy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

/**
 * Configuration spec.
 */
@ContextConfiguration(classes = SpringWebConfig.class)
@ActiveProfiles(['rest', 'rest-jms', 'core', 'jms', 'jms-rest', 'db'])
@WebAppConfiguration
class RestJmsConfigISpec extends Specification {

    @Autowired
    RestStrategy restStrategy


    def "test"() {
        expect:
            true
    }
}