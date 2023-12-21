package com.example.multidbdemo.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.multidbdemo.entities.Sample;
import com.example.multidbdemo.repositories.SampleRepository;
import com.example.multidbdemo.utils.ClientNames;
import com.example.multidbdemo.utils.DBContextHolder;

@Service
public class ClientMasterService {

  private final SampleRepository sampleRepository;

  public ClientMasterService(SampleRepository sampleRepository) {
    this.sampleRepository = sampleRepository;
  }

  public List<Sample> getDatabaseNames(String client) {
        switch (client) {
            case "db1":
                DBContextHolder.setCurrentDb(ClientNames.DB1);
                break;
            case "db2":
                DBContextHolder.setCurrentDb(ClientNames.DB2);
                break;
            case "db3":
                DBContextHolder.setCurrentDb(ClientNames.DB3);
                break;
        }
        return sampleRepository.findAll();
    }
}
