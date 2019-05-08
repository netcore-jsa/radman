package software.netcore.radman.data.conf;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import static software.netcore.radman.data.conf.Utils.buildEntityManager;


/**
 * @since v. 1.0.0
 */
@Configuration
@EnableJpaRepositories(
        basePackages = {"software.netcore.radman.data.radius.repo"},
        entityManagerFactoryRef = "radiusEntityManager",
        transactionManagerRef = "txRadius")
@EnableConfigurationProperties
public class RadiusDbConfiguration {

    private static final String RADIUS_ENTITIES_PACKAGE = "software.netcore.radman.data.radius.entity";

    @Bean
    @ConfigurationProperties("database.radius.jpa")
    JpaProperties radiusJpaProperties() {
        return new JpaProperties();
    }

    @Bean
    @ConfigurationProperties("database.radius.datasource")
    DataSource radiusDataSource() {
        return new DataSource();
    }

    @Bean
    LocalContainerEntityManagerFactoryBean radiusEntityManager() {
        return buildEntityManager(radiusDataSource(), radiusJpaProperties(),
                RADIUS_ENTITIES_PACKAGE, "radius");
    }

    @Bean(name = "txRadius")
    PlatformTransactionManager radiusTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(radiusEntityManager().getObject());
        return transactionManager;
    }

}
