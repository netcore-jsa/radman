package software.netcore.radman.buisness.service.nas.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import software.netcore.radman.buisness.service.nas.NasService;
import software.netcore.radman.buisness.service.nas.converter.DtoToNasConverter;
import software.netcore.radman.buisness.service.nas.converter.DtoToRadHuntGroupConverter;
import software.netcore.radman.buisness.service.nas.converter.NasToDtoConverter;
import software.netcore.radman.buisness.service.nas.converter.RadHuntGroupToDtoConverter;
import software.netcore.radman.data.radius.repo.NasRepo;
import software.netcore.radman.data.radius.repo.RadHuntGroupRepo;

/**
 * @since v. 1.0.0
 */
@Configuration
public class NasConfiguration {

    private final NasRepo nasRepo;
    private final RadHuntGroupRepo radHuntGroupRepo;
    private final ConversionService conversionService;

    public NasConfiguration(NasRepo nasRepo,
                            RadHuntGroupRepo radHuntGroupRepo,
                            DefaultConversionService conversionService) {
        this.nasRepo = nasRepo;
        this.radHuntGroupRepo = radHuntGroupRepo;
        this.conversionService = conversionService;

        conversionService.addConverter(new DtoToNasConverter());
        conversionService.addConverter(new NasToDtoConverter());
        conversionService.addConverter(new RadHuntGroupToDtoConverter());
        conversionService.addConverter(new DtoToRadHuntGroupConverter());
    }

    @Bean
    NasService nasService() {
        return new NasService(nasRepo, radHuntGroupRepo, conversionService);
    }

}
