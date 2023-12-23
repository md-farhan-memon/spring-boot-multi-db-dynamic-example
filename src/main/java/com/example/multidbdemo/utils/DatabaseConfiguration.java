package com.example.multidbdemo.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.example.multidbdemo.entities.MerchantDataSource;
import com.example.multidbdemo.repositories.MerchantDataSourceRepository;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
// @EnableJpaRepositories(entityManagerFactoryRef = "multiEntityManager", transactionManagerRef = "multiTransactionManager")
@EnableJpaRepositories(basePackages = "com.example.multidbdemo.repositories.*")
// @EntityScan("com.example.multidbdemo.entities")
public class DatabaseConfiguration {

    private static final String PACKAGE_SCAN = "com.example.multidbdemo.entities";

    // @Autowired
    // private BeanFactory beanFactory;

    // @Autowired
    // private MerchantDataSourceRepository merchantDataSourceRepository;

    // @Autowired
    // public DatabaseConfiguration(BeanFactory beanFactory, MerchantDataSourceRepository merchantDataSourceRepository) {
    //     this.beanFactory = beanFactory;
    //     this.merchantDataSourceRepository = merchantDataSourceRepository;
    // }

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

    @Bean(name = "multiRoutingDataSourceRedis")
    public DataSource multiRoutingDataSourceRedis(MerchantDataSourceRepository merchantDataSourceRepository, BeanFactory beanFactory) {
        List<MerchantDataSource> merchantDataSources = merchantDataSourceRepository.findAll();
        Map<Object, Object> targetDataSources = new HashMap<>();
        String primaryBeanName = "";

        merchantDataSources.forEach(dataSource -> {
            String beanName = dataSource.getBeanName();
            // if (primaryBeanName.isBlank()) {
                // primaryBeanName = beanName;
            // }
            targetDataSources.put(beanName, beanFactory.getBean(beanName));
        });

        MultiRoutingDataSource multiRoutingDataSource = new MultiRoutingDataSource();
        // multiRoutingDataSource.setDefaultTargetDataSource(beanFactory.getBean(primaryBeanName));
        multiRoutingDataSource.setTargetDataSources(targetDataSources);
        return multiRoutingDataSource;
    }

    // @Override
    // @Bean(name = "entityManager")
    // public LocalContainerEntityManagerFactoryBean entityManager() {
    //     LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    //     em.setDataSource(multiRoutingDataSource());
    //     em.setPackagesToScan(PACKAGE_SCAN);
    //     HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    //     em.setJpaVendorAdapter(vendorAdapter);
    //     em.setJpaProperties(hibernateProperties());
    //     return em;
    // }

    @Bean(name = "entityManager")
    public LocalContainerEntityManagerFactoryBean entityManager(MerchantDataSourceRepository merchantDataSourceRepository, BeanFactory beanFactory) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(multiRoutingDataSourceRedis(merchantDataSourceRepository, beanFactory));
        em.setPackagesToScan(PACKAGE_SCAN);
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(hibernateProperties());
        return em;
    }

    // @Override
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(MerchantDataSourceRepository merchantDataSourceRepository, BeanFactory beanFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManager(merchantDataSourceRepository, beanFactory).getObject());
        return transactionManager;
    }

    @Primary
    @Bean(name = "entityManagerFactory")
    public LocalSessionFactoryBean dbSessionFactory(MerchantDataSourceRepository merchantDataSourceRepository, BeanFactory beanFactory) {
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(multiRoutingDataSourceRedis(merchantDataSourceRepository, beanFactory));
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

    // @Bean
    // JedisConnectionFactory jedisConnectionFactory() {
    //     return new JedisConnectionFactory();
    // }

    // @Bean
    // public RedisTemplate<String, Object> redisTemplate() {
    //     RedisTemplate<String, Object> template = new RedisTemplate<>();
    //     template.setConnectionFactory(jedisConnectionFactory());
    //     return template;
    // }

    // @Bean
    // public DynamicDatabaseConfiguration dynamicDatabaseConfiguration(MerchantDataSourceRepository merchantDataSourceRepository) {
    // return new DynamicDatabaseConfiguration(merchantDataSourceRepository);
    // }
}
