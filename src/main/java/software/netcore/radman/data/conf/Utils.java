package software.netcore.radman.data.conf;

import liquibase.integration.spring.SpringLiquibase;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import java.util.HashMap;

/**
 * @since v. 1.0.0
 */
class Utils {

    static LocalContainerEntityManagerFactoryBean buildEntityManager(DataSource dataSource,
                                                                     JpaProperties jpaProperties,
                                                                     String entitiesPackage,
                                                                     String persistenceUnitName) {
        LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
        entityManager.setDataSource(dataSource);
        entityManager.setPackagesToScan(entitiesPackage);
        entityManager.setPersistenceUnitName(persistenceUnitName);
        entityManager.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", jpaProperties.getHibernate().getDdlAuto());
        properties.put("hibernate.dialect", jpaProperties.getHibernate().getDialect());
        properties.put("show-sql", jpaProperties.getShowSql());

        entityManager.setJpaPropertyMap(properties);
        return entityManager;
    }

    static SpringLiquibase buildLiquibase(DataSource dataSource, LiquibaseProperties properties) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(properties.getChangeLog());
        liquibase.setContexts(properties.getContexts());
        liquibase.setDefaultSchema(properties.getDefaultSchema());
        liquibase.setDropFirst(properties.isDropFirst());
        liquibase.setShouldRun(properties.isEnabled());
        liquibase.setLabels(properties.getLabels());
        liquibase.setChangeLogParameters(properties.getParameters());
        liquibase.setRollbackFile(properties.getRollbackFile());
        return liquibase;
    }

}
