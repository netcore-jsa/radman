package software.netcore.radman.data.conf;

import liquibase.integration.spring.SpringLiquibase;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import static software.netcore.radman.data.conf.Utils.buildEntityManager;
import static software.netcore.radman.data.conf.Utils.buildLiquibase;

/**
 * @since v. 1.0.0
 */
@Configuration
@EnableJpaRepositories(
        basePackages = {"software.netcore.radman.data.internal.repo"},
        entityManagerFactoryRef = "radmanEntityManager",
        transactionManagerRef = "txRadman")
@EnableConfigurationProperties
public class RadmanDbConfiguration {

    private static final String RADMAN_ENTITIES_PACKAGE = "software.netcore.radman.data.internal.entity";

    @Bean
    @ConfigurationProperties("database.radman.jpa")
    JpaProperties radmanJpaProperties() {
        return new JpaProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("database.radman.datasource")
    DataSource radmanDataSource() {
        return new DataSource();
    }

    @Bean
    @Primary
    LocalContainerEntityManagerFactoryBean radmanEntityManager() {
        return buildEntityManager(radmanDataSource(), radmanJpaProperties(),
                RADMAN_ENTITIES_PACKAGE, "radman");
    }

    @Primary
    @Bean(name = "txRadman")
    PlatformTransactionManager radmanTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(radmanEntityManager().getObject());
        return transactionManager;
    }

    @Bean
    @ConfigurationProperties(prefix = "database.radman.liquibase")
    LiquibaseProperties liquibaseProperties() {
        return new LiquibaseProperties();
    }

    @Bean
    SpringLiquibase liquibase() {
        return buildLiquibase(radmanDataSource(), liquibaseProperties());
    }

}
