package com.example.multidbdemo.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.example.multidbdemo.pojos.MerchantDataSource;
import com.example.multidbdemo.utils.MultiRoutingDataSource;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile("staticFile")
@EnableJpaRepositories
@EnableTransactionManagement
@Configuration(proxyBeanMethods = false)
@EntityScan("com.example.multidbdemo.entities")
public class DatabaseConfigurationStatic {

    private static final String PACKAGE_SCAN = "com.example.multidbdemo.entities";
    private static final String MERCHANT_DATA_SOURCE_FILE_PATH = "classpath:merchant_data_sources.json";

    @Bean
    List<MerchantDataSource> merchantDataSources(ObjectMapper objectMapper) throws IOException {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>> staticFile <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();
        return objectMapper.readValue(pathMatchingResourcePatternResolver.getResource(MERCHANT_DATA_SOURCE_FILE_PATH).getFile(), new TypeReference<List<MerchantDataSource>>(){});
    }

    @Bean(name = "multiRoutingDataSource")
    public DataSource multiRoutingDataSource(List<MerchantDataSource> merchantDataSources, BeanFactory beanFactory) {
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

    @Bean(name = "entityManager")
    public LocalContainerEntityManagerFactoryBean entityManager(List<MerchantDataSource> merchantDataSources, BeanFactory beanFactory) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(multiRoutingDataSource(merchantDataSources, beanFactory));
        em.setPackagesToScan(PACKAGE_SCAN);
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(hibernateProperties());
        return em;
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(List<MerchantDataSource> merchantDataSources, BeanFactory beanFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManager(merchantDataSources, beanFactory).getObject());
        return transactionManager;
    }

    @Primary
    @Bean(name = "entityManagerFactory")
    public LocalSessionFactoryBean dbSessionFactory(List<MerchantDataSource> merchantDataSources, BeanFactory beanFactory) {
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(multiRoutingDataSource(merchantDataSources, beanFactory));
        sessionFactoryBean.setPackagesToScan(PACKAGE_SCAN);
        sessionFactoryBean.setHibernateProperties(hibernateProperties());
        return sessionFactoryBean;
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.show_sql", true);
        properties.put("hibernate.format_sql", true);
        properties.put("hibernate.id.new_generator_mappings", false);
        properties.put("hibernate.jdbc.lob.non_contextual_creation", true);
        return properties;
    }

    @Bean
    public DynamicDatabaseConfigurationStatic dynamicDatabaseConfigurationStatic(List<MerchantDataSource> merchantDataSources) {
        return new DynamicDatabaseConfigurationStatic(merchantDataSources);
    }
}
