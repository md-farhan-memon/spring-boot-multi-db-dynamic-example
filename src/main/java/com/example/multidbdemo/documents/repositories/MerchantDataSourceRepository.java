package com.example.multidbdemo.documents.repositories;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.multidbdemo.documents.entities.MerchantDataSource;
import com.redis.om.spring.repository.RedisDocumentRepository;

@Lazy
@Repository
public interface MerchantDataSourceRepository extends CrudRepository<MerchantDataSource, String> {
}
