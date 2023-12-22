package com.example.multidbdemo.documents.entities;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import com.redis.om.spring.annotations.Document;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Lazy
@Data
@RedisHash
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(staticName = "of")
public class MerchantDataSource {

  @Id
  private String id;

  @NonNull 
  // @Indexed
  private String merchantId;

  @NonNull
  private String host;

  @NonNull
  private String port;

  @NonNull
  private String dbName;

  @NonNull
  private String username;

  @NonNull
  private String password;
}
