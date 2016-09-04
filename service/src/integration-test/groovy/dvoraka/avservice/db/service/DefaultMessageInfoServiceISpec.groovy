package dvoraka.avservice.db.service

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessageSource
import dvoraka.avservice.configuration.ServiceConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Service test.
 */
@ContextConfiguration(classes = [ServiceConfig.class])
@ActiveProfiles(["database"])
class DefaultMessageInfoServiceISpec extends Specification {

    @Autowired
    MessageInfoService messageInfoService


    def "test"() {
        expect:
            messageInfoService.save(Utils.genNormalMessage(), AvMessageSource.TEST)
    }
}
