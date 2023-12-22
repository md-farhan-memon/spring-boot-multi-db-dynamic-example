package com.example.multidbdemo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.example.multidbdemo.documents.entities.MerchantDataSource;
import com.example.multidbdemo.documents.repositories.MerchantDataSourceRepository;
import com.example.multidbdemo.repositories.SampleRepository;
import com.redis.om.spring.annotations.EnableRedisDocumentRepositories;
import com.redis.om.spring.annotations.EnableRedisEnhancedRepositories;

@SpringBootApplication
@EnableJpaRepositories
@EnableAutoConfiguration
@EnableRedisEnhancedRepositories(basePackages = "com.example.multidbdemo.documents.*")
public class MultidbdemoApplication {

	private final MerchantDataSourceRepository merchantDataSourceRepository;

  public MultidbdemoApplication(MerchantDataSourceRepository merchantDataSourceRepository) {
    this.merchantDataSourceRepository = merchantDataSourceRepository;
  }

	@Bean
	CommandLineRunner loadTestData() {
		return args -> {
			merchantDataSourceRepository.deleteAll();
			MerchantDataSource merchant1 = MerchantDataSource.of("merchant1", "localhost", "5433", "multi-db-1", "postgres", "Simpl1234");
			MerchantDataSource merchant2 = MerchantDataSource.of("merchant2", "localhost", "5433", "multi-db-2", "postgres", "Simpl1234");
			MerchantDataSource merchant3 = MerchantDataSource.of("merchant3", "localhost", "5433", "multi-db-3", "postgres", "Simpl1234");

			merchantDataSourceRepository.saveAll(List.of(merchant1, merchant2, merchant3));
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(MultidbdemoApplication.class, args);
	}

}
