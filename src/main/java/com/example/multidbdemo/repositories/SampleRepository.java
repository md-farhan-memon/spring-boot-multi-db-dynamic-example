package com.example.multidbdemo.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.multidbdemo.entities.Sample;

public interface SampleRepository extends JpaRepository<Sample, UUID> {
}
