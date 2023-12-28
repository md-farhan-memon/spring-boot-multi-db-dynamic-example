package com.example.multidbdemo.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.multidbdemo.redis.MerchantDataSource;
import com.example.multidbdemo.redis.MerchantDataSourceRepository;

// @Service
@Component
public class MerchantConfigService {
  private final MerchantDataSourceRepository merchantDataSourceRepository;

  @Autowired
  public MerchantConfigService(MerchantDataSourceRepository merchantDataSourceRepository) {
    this.merchantDataSourceRepository = merchantDataSourceRepository;
  }

  public List<MerchantDataSource> getAllConfig() {
    return merchantDataSourceRepository.findAll();
  }

  public List<MerchantDataSource> deleteAllConfig() {
    merchantDataSourceRepository.deleteAll();
    return getAllConfig();
  }

  public MerchantDataSource addConfig(MerchantDataSource merchantDataSource) {
    return merchantDataSourceRepository.save(merchantDataSource);
  }
}
