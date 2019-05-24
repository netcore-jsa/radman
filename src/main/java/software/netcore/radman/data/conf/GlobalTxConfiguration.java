package software.netcore.radman.data.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @since v. 1.0.0
 */
@Configuration
public class GlobalTxConfiguration {

    private final PlatformTransactionManager radiusTxManager;
    private final PlatformTransactionManager radmanTxManager;

    @Autowired
    public GlobalTxConfiguration(@Qualifier("txRadius") PlatformTransactionManager radiusTxManager,
                                 @Qualifier("txRadman") PlatformTransactionManager radmanTxManager) {
        this.radiusTxManager = radiusTxManager;
        this.radmanTxManager = radmanTxManager;
    }

    @Bean
    PlatformTransactionManager transactionManager() {
        return new ChainedTransactionManager(radiusTxManager, radmanTxManager);
    }

}
