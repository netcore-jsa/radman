package software.netcore.radman.buisness.service.user.radius.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import software.netcore.radman.buisness.service.user.radius.RadiusUserService;
import software.netcore.radman.buisness.service.user.radius.converter.DtoToRadiusGroupConverter;
import software.netcore.radman.buisness.service.user.radius.converter.DtoToRadiusUserConverter;
import software.netcore.radman.buisness.service.user.radius.converter.RadiusGroupToDtoConverter;
import software.netcore.radman.buisness.service.user.radius.converter.RadiusUserToDtoConverter;
import software.netcore.radman.data.internal.repo.RadiusGroupRepo;
import software.netcore.radman.data.internal.repo.RadiusUserRepo;
import software.netcore.radman.data.radius.repo.RadUserGroupRepo;

/**
 * @since v. 1.0.0
 */
@Configuration
public class RadiusUserServiceConfiguration {

    private final RadiusUserRepo radiusUserRepo;
    private final RadiusGroupRepo radiusGroupRepo;
    private final RadUserGroupRepo radUserGroupRepo;
    private final ConversionService conversionService;

    @Autowired
    public RadiusUserServiceConfiguration(RadiusUserRepo radiusUserRepo,
                                          RadiusGroupRepo radiusGroupRepo,
                                          RadUserGroupRepo radUserGroupRepo,
                                          DefaultConversionService conversionService) {
        this.radiusUserRepo = radiusUserRepo;
        this.radiusGroupRepo = radiusGroupRepo;
        this.radUserGroupRepo = radUserGroupRepo;
        this.conversionService = conversionService;

        conversionService.addConverter(new RadiusUserToDtoConverter());
        conversionService.addConverter(new DtoToRadiusUserConverter());
        conversionService.addConverter(new RadiusGroupToDtoConverter());
        conversionService.addConverter(new DtoToRadiusGroupConverter());
    }

    @Bean
    RadiusUserService radiusUserService() {
        return new RadiusUserService(radiusUserRepo, radiusGroupRepo, radUserGroupRepo, conversionService);
    }

}
