package software.netcore.radman.buisness.service.user.system.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.security.crypto.password.PasswordEncoder;
import software.netcore.radman.buisness.service.user.system.SystemUserService;
import software.netcore.radman.buisness.service.user.system.converter.*;
import software.netcore.radman.data.internal.repo.SystemUserRepo;

/**
 * @since v. 1.0.0
 */
@Configuration
public class SystemServiceConfiguration {

    private final SystemUserRepo systemUserRepo;
    private final PasswordEncoder passwordEncoder;
    private final ConversionService conversionService;

    @Autowired
    public SystemServiceConfiguration(SystemUserRepo systemUserRepo,
                                      PasswordEncoder passwordEncoder,
                                      DefaultConversionService conversionService) {
        this.systemUserRepo = systemUserRepo;
        this.passwordEncoder = passwordEncoder;
        this.conversionService = conversionService;

        conversionService.addConverter(new SystemUserToDtoConverter(conversionService));
        conversionService.addConverter(new DtoToSystemUserConverter(conversionService));
        conversionService.addConverter(new AuthProviderToDtoConverter());
        conversionService.addConverter(new DtoToAuthProviderConverter());
        conversionService.addConverter(new RoleToDtoConverter());
        conversionService.addConverter(new DtoToRoleConverter());
    }

    @Bean
    SystemUserService systemUserService() {
        return new SystemUserService(systemUserRepo, conversionService, passwordEncoder);
    }

}
