package com.aston.frontendpracticeservice;

import com.aston.frontendpracticeservice.kafka.config.properties.KafkaProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(KafkaProperties.class)
public class FrontendPracticeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrontendPracticeServiceApplication.class, args);
    }

}