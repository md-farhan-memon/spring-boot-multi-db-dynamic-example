package com.example.multidbdemo.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.service.spi.InjectService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.example.multidbdemo.redis.MerchantDataSource;
import com.example.multidbdemo.redis.MerchantDataSourceRepository;
import com.example.multidbdemo.services.MerchantConfigService;
import com.example.multidbdemo.utils.MultiRoutingDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile("redis")
// @EnableJpaRepositories
@EnableTransactionManagement
@Configuration(proxyBeanMethods = false)
// @EntityScan("com.example.multidbdemo.entities")
public class DatabaseConfigurationRedis {

    private static final String PACKAGE_SCAN = "com.example.multidbdemo.entities";

    // @InjectService
    // MerchantDataSourceRepository merchantDataSourceRepository;

    // private final MerchantConfigService merchantConfigService;

    // public DatabaseConfigurationRedis(ObjectProvider<MerchantConfigService> merchantConfigServiceProvider) {
    //     this.merchantConfigService = merchantConfigServiceProvider.getObject();
    // }

    // @Bean
    // @Autowired
    // List<MerchantDataSource> merchantDataSources(ObjectProvider<MerchantConfigService> merchantConfigServiceProvider) {
    //     log.info(">>>>>>>>>>>>>>>>>>>>>>>> redis <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    //     // return merchantDataSourceRepository.findAll();
    //     return merchantConfigServiceProvider.getObject().getAllConfig();
    // }

    // @Bean
    // @Autowired
    // List<MerchantDataSource> merchantDataSources(final MerchantConfigService merchantConfigService) {
    //     log.info(">>>>>>>>>>>>>>>>>>>>>>>> redis <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    //     // return merchantDataSourceRepository.findAll();
    //     return merchantConfigService.getAllConfig();
    // }

    // @Bean
    // public RedisTemplate<String, RateRedisEntry> redisTemplate() {
    // RedisTemplate<String, RateRedisEntry> template = new RedisTemplate<>();
            
    // template.setConnectionFactory(getLettuceConnectionFactory());
            
    // return template;
    // }

    @Bean
    // @Autowired
    List<MerchantDataSource> merchantDataSources(final MerchantDataSourceRepository merchantDataSourceRepository) {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>> redis <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        return merchantDataSourceRepository.findAll();
        // return merchantConfigService.getAllConfig();
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
    public DynamicDatabaseConfigurationRedis dynamicDatabaseConfigurationRedis(List<MerchantDataSource> merchantDataSources) {
        return new DynamicDatabaseConfigurationRedis(merchantDataSources);
    }
}
