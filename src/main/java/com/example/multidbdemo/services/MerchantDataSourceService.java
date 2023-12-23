package com.example.multidbdemo.services;

import org.springframework.stereotype.Service;

import com.example.multidbdemo.entities.MerchantDataSource;
import com.example.multidbdemo.repositories.MerchantDataSourceRepository;

@Service
public class MerchantDataSourceService {

  private final MerchantDataSourceRepository merchantDataSourceRepository;

  public MerchantDataSourceService(MerchantDataSourceRepository merchantDataSourceRepository) {
    this.merchantDataSourceRepository = merchantDataSourceRepository;
  }

  public MerchantDataSource getSourceById(String id) {
    return merchantDataSourceRepository.findById(id).get();
  }
}
