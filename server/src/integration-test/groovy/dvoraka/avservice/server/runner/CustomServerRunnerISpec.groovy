package dvoraka.avservice.server.runner

import spock.lang.Specification

/**
 * Test for server running.
 */
class CustomServerRunnerISpec extends Specification {

    def "Run custom server runner"() {
        when:
            CustomServerRunner.setTestRun(true)
            CustomServerRunner.main([] as String[])

        then:
            notThrown(Exception)
    }
}
