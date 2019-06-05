package software.netcore.radman.buisness.service.security.conf;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.netcore.radman.buisness.service.security.SecurityService;
import software.netcore.radman.data.internal.repo.SystemUserRepo;
import software.netcore.radman.security.fallback.SingleUserDetailsManager;

/**
 * @since v. 1.0.0
 */
@Configuration
@RequiredArgsConstructor
public class SecurityServiceConfiguration {

    private final SingleUserDetailsManager userDetailsManager;
    private final SystemUserRepo systemUserRepo;

    @Bean
    SecurityService securityService() {
        return new SecurityService(userDetailsManager, systemUserRepo);
    }

}
