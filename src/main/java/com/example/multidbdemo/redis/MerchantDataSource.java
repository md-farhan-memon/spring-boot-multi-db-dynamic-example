package com.example.multidbdemo.redis;

import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@RedisHash("MerchantDataSource")
public class MerchantDataSource {
  private String id; // merchantId
  private String dbUrl;
  private String dbUsername;
  private String dbPassword;

  public String getBeanName() {
    return "merchantDBSource_" + this.getId();
  }

  public boolean isValid() {
    return isPresent(id) && isPresent(dbUrl) && isPresent(dbPassword) && isPresent(dbUsername);
  }

  private boolean isPresent(String string) {
    return string != null && !string.isBlank();
  }
}
