package com.example.multidbdemo.entities;

import java.io.Serializable;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Data;

// @Lazy
@Data
@AllArgsConstructor
@RedisHash("MerchantDataSource")
public class MerchantDataSource implements Serializable {

  private String id;
  private String host;
  private String port;
  private String dbName;
  private String username;
  private String password;

  public String getBeanName() {
    return "merchantDBSource_" + this.getId();
  }
}
