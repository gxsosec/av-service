package dvoraka.avservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * App configuration.
 */
@Configuration
public class AppConfig {

    @Bean
    public AVService avService() {
        return null;
    }

    @Bean
    public AVProgram avProgram() {
        return null;
    }

    @Bean
    public AVServer avServer() {
        return null;
    }
}