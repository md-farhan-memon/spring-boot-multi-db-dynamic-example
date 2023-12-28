package com.example.multidbdemo.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.multidbdemo.redis.MerchantDataSource;
import com.example.multidbdemo.services.MerchantConfigService;

@RestController
@RequestMapping("/config")
public class MerchantConfigController {
  private final MerchantConfigService merchantConfigService;

  public MerchantConfigController(MerchantConfigService merchantConfigService) {
    this.merchantConfigService = merchantConfigService;
  }

  @GetMapping("/all")
  public List<MerchantDataSource> getAll() {
    return merchantConfigService.getAllConfig();
  }

  @DeleteMapping("/deleteAll")
  public List<MerchantDataSource> deleteAll() {
    return merchantConfigService.deleteAllConfig();
  }

  @PostMapping("/add")
  public ResponseEntity<MerchantDataSource> addDataSource(@RequestBody MerchantDataSource merchantDataSource) {
    if (merchantDataSource.isValid()) {
      return new ResponseEntity<>(merchantConfigService.addConfig(merchantDataSource), HttpStatus.CREATED);
    } else {
      return new ResponseEntity<>(merchantDataSource, HttpStatus.BAD_REQUEST);
    }
  }
}
