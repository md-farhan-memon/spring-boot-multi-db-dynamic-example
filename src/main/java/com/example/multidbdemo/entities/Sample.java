package com.example.multidbdemo.entities;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "sample")
public class Sample implements Serializable {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "name")
  private String name;
}
