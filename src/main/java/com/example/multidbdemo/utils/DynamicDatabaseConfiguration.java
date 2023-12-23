package com.example.multidbdemo.utils;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.example.multidbdemo.entities.MerchantDataSource;
import com.example.multidbdemo.repositories.MerchantDataSourceRepository;

import lombok.NoArgsConstructor;

@Configuration
public class DynamicDatabaseConfiguration implements BeanDefinitionRegistryPostProcessor {
  private final MerchantDataSourceRepository merchantDataSourceRepository;

  @Autowired
  public DynamicDatabaseConfiguration(MerchantDataSourceRepository merchantDataSourceRepository) {
    this.merchantDataSourceRepository = merchantDataSourceRepository;
  }

  // @Bean
  // public BeanDefinitionRegistrar beanDefinitionRegistrar(MerchantDataSourceRepository merchantDataSourceRepository) {
  //   return new BeanDefinitionRegistrar(merchantDataSourceRepository);
  // }

  // public class BeanDefinitionRegistrar implements BeanDefinitionRegistryPostProcessor {
    // private final MerchantDataSourceRepository merchantDataSourceRepository;
  
    // @Autowired
    // public BeanDefinitionRegistrar(MerchantDataSourceRepository merchantDataSourceRepository) {
    //   this.merchantDataSourceRepository = merchantDataSourceRepository;
    // }
  
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
      List<MerchantDataSource> merchantDataSources = merchantDataSourceRepository.findAll();
  
      merchantDataSources.forEach(dataSource -> {
        GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
        genericBeanDefinition.setBeanClass(DataSource.class);
        genericBeanDefinition.setInstanceSupplier(() -> DataSourceBuilder.create()
          .url("jdbc:postgresql://" + dataSource.getHost() + ":" + dataSource.getPort() + "/" + dataSource.getDbName())
          .username(dataSource.getUsername())
          .password(dataSource.getPassword())
          .build()
        );
        registry.registerBeanDefinition(dataSource.getBeanName(), genericBeanDefinition);
      });
    }
  
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
      // blah
    }
  // }
}
