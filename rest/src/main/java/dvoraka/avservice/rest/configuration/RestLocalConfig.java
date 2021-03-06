package dvoraka.avservice.rest.configuration;

import dvoraka.avservice.MessageProcessor;
import dvoraka.avservice.rest.service.LocalRestService;
import dvoraka.avservice.rest.service.RestService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * REST local configuration.
 */
@Configuration
@Profile("rest-local")
public class RestLocalConfig {

    @Bean
    public RestService avRestService(MessageProcessor checkAndFileProcessor) {
        return new LocalRestService(checkAndFileProcessor);
    }
}
