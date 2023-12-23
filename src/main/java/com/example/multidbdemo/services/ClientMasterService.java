package com.example.multidbdemo.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.multidbdemo.entities.Sample;
import com.example.multidbdemo.repositories.SampleRepository;
import com.example.multidbdemo.utils.DBContextHolder;

@Service
public class ClientMasterService {

    private final SampleRepository sampleRepository;

    public ClientMasterService(SampleRepository sampleRepository) {
        this.sampleRepository = sampleRepository;
    }

    public List<Sample> getFromDatabase(String merchantId) {
        DBContextHolder.setCurrentDb("merchantDBSource_" + merchantId);
        return sampleRepository.findAll();
    }
}
