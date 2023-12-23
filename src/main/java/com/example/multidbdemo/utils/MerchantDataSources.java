package com.example.multidbdemo.utils;

import lombok.Data;

@Data
public class MerchantDataSources {
  private String merchantId;
  private String dbUrl;
  private String dbUsername;
  private String dbPassword;

  public String getBeanName() {
    return "merchantDBSource_" + this.getMerchantId();
  }
}
