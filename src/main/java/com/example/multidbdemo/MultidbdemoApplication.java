package com.example.multidbdemo;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import com.example.multidbdemo.entities.MerchantDataSource;
import com.example.multidbdemo.repositories.MerchantDataSourceRepository;

// @ComponentScan(basePackages = "com.example.multidbdemo.documents.*")
@EnableJpaRepositories(basePackages = "com.example.multidbdemo.repositories.*")
// @EnableRedisRepositories(basePackages = "com.example.multidbdemo.documents.*")
// @DependsOn("jedisConnectionFactory")
@EnableAutoConfiguration
@SpringBootApplication
public class MultidbdemoApplication {

	private final MerchantDataSourceRepository merchantDataSourceRepository;

	public MultidbdemoApplication(MerchantDataSourceRepository merchantDataSourceRepository) {
		this.merchantDataSourceRepository = merchantDataSourceRepository;
	}

	@Bean
	CommandLineRunner loadTestData() {
		return args -> {
			merchantDataSourceRepository.deleteAll();

			MerchantDataSource merchantDataSource1 = new MerchantDataSource("merchant-id-1", "localhost", "5433", "multi-db-1", "postgres", "Simpl1234");
			MerchantDataSource merchantDataSource2 = new MerchantDataSource("merchant-id-2", "localhost", "5433", "multi-db-2", "postgres", "Simpl1234");
			MerchantDataSource merchantDataSource3 = new MerchantDataSource("merchant-id-3", "localhost", "5433", "multi-db-3", "postgres", "Simpl1234");

			merchantDataSourceRepository.saveAll(List.of(merchantDataSource1, merchantDataSource2, merchantDataSource3));
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(MultidbdemoApplication.class, args);
	}

}
