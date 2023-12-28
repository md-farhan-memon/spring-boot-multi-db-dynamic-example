package com.example.multidbdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@EnableAutoConfiguration
@SpringBootApplication(exclude = RedisRepositoriesAutoConfiguration.class)
public class MultidbdemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MultidbdemoApplication.class, args);
	}

}
