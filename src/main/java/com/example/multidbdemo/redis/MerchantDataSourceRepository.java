package com.example.multidbdemo.redis;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantDataSourceRepository extends CrudRepository<MerchantDataSource, String> {
  public List<MerchantDataSource> findAll();
}
