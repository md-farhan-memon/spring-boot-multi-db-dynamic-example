package com.example.multidbdemo.config;

import java.util.List;
import java.util.stream.IntStream;

import javax.sql.DataSource;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.jdbc.DataSourceBuilder;

import com.example.multidbdemo.pojos.MerchantDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DynamicDatabaseConfiguration implements BeanDefinitionRegistryPostProcessor {
  private final List<MerchantDataSource> merchantDataSources;

  public DynamicDatabaseConfiguration(List<MerchantDataSource> merchantDataSources) {
    this.merchantDataSources = merchantDataSources;
  }

  @Override
  public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
    IntStream.range(0, merchantDataSources.size()).forEach(idx -> {
      MerchantDataSource dataSource = merchantDataSources.get(idx);
      GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
      genericBeanDefinition.setBeanClass(DataSource.class);
      genericBeanDefinition.setInstanceSupplier(
        () -> DataSourceBuilder.create()
        .url(dataSource.getDbUrl())
        .username(dataSource.getDbUsername())
        .password(dataSource.getDbPassword())
        .build()
      );

      if (idx == 0) {
        genericBeanDefinition.setPrimary(true);
      }

      registry.registerBeanDefinition(dataSource.getBeanName(), genericBeanDefinition);
      log.info("Created Bean >>>>>>>>> " + dataSource.getBeanName() + " | Primary " + genericBeanDefinition.isPrimary());
    });
  }
}
