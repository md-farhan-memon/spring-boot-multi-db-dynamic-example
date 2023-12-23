package com.example.multidbdemo.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
// @EnableJpaRepositories(entityManagerFactoryRef = "multiEntityManager", transactionManagerRef = "multiTransactionManager")
@EnableJpaRepositories
@EntityScan("com.example.multidbdemo.entities")
public class DatabaseConfiguration {

    private static final String PACKAGE_SCAN = "com.example.multidbdemo.entities";
    private static final String MERCHANT_DATA_SOURCE_FILE_PATH = "classpath:merchant_data_sources.json";

    @Bean
    List<MerchantDataSources> merchantDataSources(ObjectMapper objectMapper) throws IOException {
        PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();
        return objectMapper.readValue(pathMatchingResourcePatternResolver.getResource(MERCHANT_DATA_SOURCE_FILE_PATH).getFile(), new TypeReference<List<MerchantDataSources>>(){});
    }

    // @Primary
    // @Bean(name = "db1DataSource")
    // @ConfigurationProperties("app.datasource.db1")
    // public DataSource db1DataSource() {
    //     return DataSourceBuilder.create().type(HikariDataSource.class).build();
    // }

    // @Bean(name = "db2DataSource")
    // @ConfigurationProperties("app.datasource.db2")
    // public DataSource db2DataSource() {
    //     return DataSourceBuilder.create().type(HikariDataSource.class).build();
    // }

    // @Bean(name = "db3DataSource")
    // @ConfigurationProperties("app.datasource.db3")
    // public DataSource db3DataSource() {
    //     return DataSourceBuilder.create().type(HikariDataSource.class).build();
    // }

    // @Bean(name = "multiRoutingDataSource")
    // public DataSource multiRoutingDataSource() {
    //     Map<Object, Object> targetDataSources = new HashMap<>();
    //     targetDataSources.put(ClientNames.DB1, db1DataSource());
    //     targetDataSources.put(ClientNames.DB2, db2DataSource());
    //     targetDataSources.put(ClientNames.DB3, db3DataSource());
    //     MultiRoutingDataSource multiRoutingDataSource = new MultiRoutingDataSource();
    //     multiRoutingDataSource.setDefaultTargetDataSource(db1DataSource());
    //     multiRoutingDataSource.setTargetDataSources(targetDataSources);
    //     return multiRoutingDataSource;
    // }

    @Bean(name = "multiRoutingDataSource")
    public DataSource multiRoutingDataSource(List<MerchantDataSources> merchantDataSources, BeanFactory beanFactory) {
        Map<Object, Object> targetDataSources = new HashMap<>();

        merchantDataSources.forEach(dataSource -> {
            String beanName = dataSource.getBeanName();
            targetDataSources.put(beanName, beanFactory.getBean(beanName));
        });

        MultiRoutingDataSource multiRoutingDataSource = new MultiRoutingDataSource();
        multiRoutingDataSource.setDefaultTargetDataSource(beanFactory.getBean(merchantDataSources.get(0).getBeanName()));
        multiRoutingDataSource.setTargetDataSources(targetDataSources);
        multiRoutingDataSource.afterPropertiesSet();
        return multiRoutingDataSource;
    }

    // @Override
    @Bean(name = "entityManager")
    public LocalContainerEntityManagerFactoryBean entityManager(List<MerchantDataSources> merchantDataSources, BeanFactory beanFactory) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(multiRoutingDataSource(merchantDataSources, beanFactory));
        em.setPackagesToScan(PACKAGE_SCAN);
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(hibernateProperties());
        return em;
    }

    // @Override
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(List<MerchantDataSources> merchantDataSources, BeanFactory beanFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManager(merchantDataSources, beanFactory).getObject());
        return transactionManager;
    }

    @Primary
    @Bean(name = "entityManagerFactory")
    public LocalSessionFactoryBean dbSessionFactory(List<MerchantDataSources> merchantDataSources, BeanFactory beanFactory) {
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(multiRoutingDataSource(merchantDataSources, beanFactory));
        sessionFactoryBean.setPackagesToScan(PACKAGE_SCAN);
        sessionFactoryBean.setHibernateProperties(hibernateProperties());
        return sessionFactoryBean;
    }

    // add hibernate properties
    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.show_sql", true);
        properties.put("hibernate.format_sql", true);
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.id.new_generator_mappings", false);
        properties.put("hibernate.jdbc.lob.non_contextual_creation", true);
        return properties;
    }

    @Bean
    public DynamicDatabaseConfiguration dynamicDatabaseConfiguration(List<MerchantDataSources> merchantDataSources) {
        return new DynamicDatabaseConfiguration(merchantDataSources);
    }
}
