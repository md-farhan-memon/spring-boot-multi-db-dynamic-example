package com.example.multidbdemo.repositories;

import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.example.multidbdemo.entities.MerchantDataSource;

// @Lazy
// @Service
// @Component
@Repository
public interface MerchantDataSourceRepository extends CrudRepository<MerchantDataSource, String> {
  public List<MerchantDataSource> findAll();
}
