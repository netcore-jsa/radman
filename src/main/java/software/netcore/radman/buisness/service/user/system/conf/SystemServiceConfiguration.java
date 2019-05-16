package software.netcore.radman.buisness.service.user.system.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import software.netcore.radman.buisness.service.user.system.SystemUserService;
import software.netcore.radman.buisness.service.user.system.converter.DtoToSystemUserConverter;
import software.netcore.radman.buisness.service.user.system.converter.SystemUserToDtoConverter;
import software.netcore.radman.data.internal.repo.SystemUserRepo;

/**
 * @since v. 1.0.0
 */
@Configuration
public class SystemServiceConfiguration {

    private final SystemUserRepo systemUserRepo;
    private final ConversionService conversionService;

    @Autowired
    public SystemServiceConfiguration(SystemUserRepo systemUserRepo,
                                      DefaultConversionService conversionService) {
        this.systemUserRepo = systemUserRepo;
        this.conversionService = conversionService;

        conversionService.addConverter(new SystemUserToDtoConverter());
        conversionService.addConverter(new DtoToSystemUserConverter());
    }

    @Bean
    SystemUserService systemUserService() {
        return new SystemUserService(systemUserRepo, conversionService);
    }

}
