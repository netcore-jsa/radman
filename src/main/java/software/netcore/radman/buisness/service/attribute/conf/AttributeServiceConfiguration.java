package software.netcore.radman.buisness.service.attribute.conf;

import org.springframework.beans.factory.annotation.Autowired;
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
import software.netcore.radman.data.radius.repo.RadCheckRepo;
import software.netcore.radman.data.radius.repo.RadGroupCheckRepo;
import software.netcore.radman.data.radius.repo.RadGroupReplyRepo;
import software.netcore.radman.data.radius.repo.RadReplyRepo;

/**
 * @since v. 1.0.0
 */
@Configuration
public class AttributeServiceConfiguration {

    private final RadCheckAttributeRepo checkAttributeRepo;
    private final RadReplyAttributeRepo replyAttributeRepo;

    private final RadCheckRepo radCheckRepo;
    private final RadReplyRepo radReplyRepo;
    private final RadGroupCheckRepo radGroupCheckRepo;
    private final RadGroupReplyRepo radGroupReplyRepo;

    private final ConversionService conversionService;

    @Autowired
    public AttributeServiceConfiguration(RadCheckAttributeRepo checkAttributeRepo,
                                         RadReplyAttributeRepo replyAttributeRepo,
                                         RadCheckRepo radCheckRepo,
                                         RadReplyRepo radReplyRepo,
                                         RadGroupCheckRepo radGroupCheckRepo,
                                         RadGroupReplyRepo radGroupReplyRepo,
                                         DefaultConversionService conversionService) {
        this.checkAttributeRepo = checkAttributeRepo;
        this.replyAttributeRepo = replyAttributeRepo;
        this.radCheckRepo = radCheckRepo;
        this.radReplyRepo = radReplyRepo;
        this.radGroupCheckRepo = radGroupCheckRepo;
        this.radGroupReplyRepo = radGroupReplyRepo;
        this.conversionService = conversionService;

        conversionService.addConverter(new RadCheckAttributeToDtoConverter());
        conversionService.addConverter(new DtoToRadCheckAttributeConverter());
        conversionService.addConverter(new RadReplyAttributeToDtoConverter());
        conversionService.addConverter(new DtoToRadReplyAttributeConverter());
    }

    @Bean
    AttributeService attributeService() {
        return new AttributeService(checkAttributeRepo, replyAttributeRepo, radCheckRepo,
                radReplyRepo, radGroupCheckRepo, radGroupReplyRepo, conversionService);
    }

}
