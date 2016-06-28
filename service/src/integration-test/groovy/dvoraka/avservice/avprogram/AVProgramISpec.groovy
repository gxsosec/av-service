package dvoraka.avservice.avprogram

import dvoraka.avservice.avprogram.AVProgram
import dvoraka.avservice.common.Utils
import dvoraka.avservice.configuration.AppConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * AVProgram test.
 */
@ContextConfiguration(classes = [AppConfig])
class AVProgramISpec extends Specification {

    @Shared
    String eicarString = Utils.EICAR

    @Autowired
    AVProgram avProgram


    def setup() {
    }

    def "AV program loading"() {
        expect:
        avProgram != null
    }

    def "Is program running?"() {
        expect:
        avProgram.isRunning()
    }

    def "scan normal bytes"() {
        setup:
        byte[] bytes = "No virus here".getBytes("UTF-8")

        when:
        boolean shouldBeFalse = avProgram.scanStream(bytes)

        then:
        !shouldBeFalse
    }

    def "scan eicar bytes"() {
        setup:
        byte[] bytes = eicarString.getBytes("UTF-8")

        when:
        boolean shouldBeTrue = avProgram.scanStream(bytes)

        then:
        shouldBeTrue
    }

    @Ignore
    @Unroll
    def "stream length test: #size bytes"() {
        setup:
        byte[] bytes = new byte[size];

        expect:
        !avProgram.scanStream(bytes)

        where:
        size << [100, 1000, 10_000, 100_000, 1000_000, 10_000_000]
    }

    def "parallel scan"() {

    }
}