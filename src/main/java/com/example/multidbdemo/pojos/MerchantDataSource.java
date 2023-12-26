package com.example.multidbdemo.pojos;

import lombok.Data;

@Data
public class MerchantDataSource {
  private String merchantId;
  private String dbUrl;
  private String dbUsername;
  private String dbPassword;

  public String getBeanName() {
    return "merchantDBSource_" + this.getMerchantId();
  }
}
