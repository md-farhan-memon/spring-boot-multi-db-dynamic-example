package com.example.multidbdemo.utils;

import java.util.List;
import java.util.stream.IntStream;

import javax.sql.DataSource;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.jdbc.DataSourceBuilder;

public class DynamicDatabaseConfiguration implements BeanDefinitionRegistryPostProcessor {
  private final List<MerchantDataSources> merchantDataSources;

  public DynamicDatabaseConfiguration(List<MerchantDataSources> merchantDataSources) {
    this.merchantDataSources = merchantDataSources;
  }

  @Override
  public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
    IntStream.range(0, merchantDataSources.size()).forEach(idx -> {
      MerchantDataSources dataSource = merchantDataSources.get(idx);
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
      System.out.println(">>>>>>>>>>>>>>>>>>> Created >>>>>>>>> " + dataSource.getBeanName() + " | Primary " + genericBeanDefinition.isPrimary());
    });
  }
}
