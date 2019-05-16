package software.netcore.radman.buisness.service.accounting.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import software.netcore.radman.buisness.service.accounting.AccountingService;
import software.netcore.radman.buisness.service.accounting.converter.RadAcctToDtoConverter;
import software.netcore.radman.data.radius.repo.RadAcctRepo;

/**
 * @since v. 1.0.0
 */
@Configuration
public class AccountingServiceConfiguration {

    private final RadAcctRepo radAcctRepo;
    private final ConversionService conversionService;

    @Autowired
    public AccountingServiceConfiguration(RadAcctRepo radAcctRepo,
                                          DefaultConversionService conversionService) {
        this.radAcctRepo = radAcctRepo;
        this.conversionService = conversionService;

        conversionService.addConverter(new RadAcctToDtoConverter());
    }

    @Bean
    AccountingService accountingService() {
        return new AccountingService(radAcctRepo, conversionService);
    }

}
