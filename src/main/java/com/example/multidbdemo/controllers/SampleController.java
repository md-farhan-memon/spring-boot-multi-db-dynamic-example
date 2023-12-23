package com.example.multidbdemo.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.multidbdemo.entities.MerchantDataSource;
import com.example.multidbdemo.entities.Sample;
import com.example.multidbdemo.services.ClientMasterService;
import com.example.multidbdemo.services.MerchantDataSourceService;

@RestController
@RequestMapping("/sample")
public class SampleController {

  private final ClientMasterService clientMasterService;
  private final MerchantDataSourceService merchantDataSourceService;

  public SampleController(ClientMasterService clientMasterService, MerchantDataSourceService merchantDataSourceService) {
    this.clientMasterService = clientMasterService;
    this.merchantDataSourceService = merchantDataSourceService;
  }

  @GetMapping("fromDB/{id}")
  public List<Sample> findAllFromDb(@PathVariable String id) {
    return clientMasterService.getDatabaseNames("merchantDBSource_" + id);
  }

  @GetMapping("fromRedis/{id}")
  public MerchantDataSource findOneFromRedis(@PathVariable String id) {
    return merchantDataSourceService.getSourceById(id);
  }
}
