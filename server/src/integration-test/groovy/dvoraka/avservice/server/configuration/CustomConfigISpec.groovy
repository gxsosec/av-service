package dvoraka.avservice.server.configuration

import dvoraka.avservice.common.SpringUtils
import dvoraka.avservice.server.configuration.jms.JmsConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.support.AbstractApplicationContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore
import spock.lang.Specification

/**
 * Custom configuration test.
 */
@Ignore("manual testing")
@ContextConfiguration(classes = [JmsConfig.class])
@ActiveProfiles(['jms', 'jms-client'])
class CustomConfigISpec extends Specification {

    @Autowired
    AbstractApplicationContext applicationContext


    def "show context"() {
        setup:
            SpringUtils.printBeansList(applicationContext)

        expect:
            true
    }
}
