package software.netcore.radman.buisness.service.attribute.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import software.netcore.radman.buisness.service.attribute.AttributeService;
import software.netcore.radman.buisness.service.attribute.converter.DtoToRadCheckAttributeConverter;
import software.netcore.radman.buisness.service.attribute.converter.DtoToRadReplyAttributeConverter;
import software.netcore.radman.buisness.service.attribute.converter.RadCheckAttributeToDtoConverter;
import software.netcore.radman.buisness.service.attribute.converter.RadReplyAttributeToDtoConverter;
import software.netcore.radman.data.internal.repo.RadCheckAttributeRepo;
import software.netcore.radman.data.internal.repo.RadReplyAttributeRepo;

/**
 * @since v. 1.0.0
 */
@Configuration
public class AttributeConfiguration {

    private final RadCheckAttributeRepo checkAttributeRepo;
    private final RadReplyAttributeRepo replyAttributeRepo;
    private final ConversionService conversionService;

    public AttributeConfiguration(RadCheckAttributeRepo checkAttributeRepo,
                                  RadReplyAttributeRepo replyAttributeRepo,
                                  DefaultConversionService conversionService) {
        this.checkAttributeRepo = checkAttributeRepo;
        this.replyAttributeRepo = replyAttributeRepo;
        this.conversionService = conversionService;

        conversionService.addConverter(new RadCheckAttributeToDtoConverter());
        conversionService.addConverter(new DtoToRadCheckAttributeConverter());
        conversionService.addConverter(new RadReplyAttributeToDtoConverter());
        conversionService.addConverter(new DtoToRadReplyAttributeConverter());
    }

    @Bean
    AttributeService attributeService() {
        return new AttributeService(checkAttributeRepo, replyAttributeRepo, conversionService);
    }

}
