package ee.MinuTood.Quest.registration.system.configurations;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuratsiooni klass ModelMapperi Beani loomiseks
 *
 * @author Tanel Sepp
 */
@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
