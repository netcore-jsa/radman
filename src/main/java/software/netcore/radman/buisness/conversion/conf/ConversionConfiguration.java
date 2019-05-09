package software.netcore.radman.buisness.conversion.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.DefaultConversionService;

/**
 * @since v. 1.0.0
 */
@Configuration
public class ConversionConfiguration {

    @Bean
    DefaultConversionService defaultConversionService() {
        return new DefaultConversionService();
    }

}
