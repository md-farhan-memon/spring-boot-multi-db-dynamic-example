// package com.example.multidbdemo.utils;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
// import org.springframework.data.redis.core.RedisTemplate;

// import com.example.multidbdemo.documents.MerchantDataSource;

// @Configuration
// public class RedisConfig {
//     @Bean
//     JedisConnectionFactory jedisConnectionFactory() {
//         return new JedisConnectionFactory();
//     }

//     @Bean
//     public RedisTemplate<String, MerchantDataSource> redisTemplate() {
//         RedisTemplate<String, MerchantDataSource> template = new RedisTemplate<>();
//         jedisConnectionFactory().start();
//         template.setConnectionFactory(jedisConnectionFactory());
//         return template;
//     }
// }
