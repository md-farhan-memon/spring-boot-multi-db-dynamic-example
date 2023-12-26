package com.example.multidbdemo.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.multidbdemo.entities.Sample;
import com.example.multidbdemo.services.ClientMasterService;

@RestController
@RequestMapping("/sample")
public class SampleController {

  private final ClientMasterService clientMasterService;

  public SampleController(ClientMasterService clientMasterService) {
    this.clientMasterService = clientMasterService;
  }

  @GetMapping("/{merchantId}")
  public List<Sample> findAllFromDb(@PathVariable String merchantId) {
    return clientMasterService.getFromDatabase(merchantId);
  }
}
