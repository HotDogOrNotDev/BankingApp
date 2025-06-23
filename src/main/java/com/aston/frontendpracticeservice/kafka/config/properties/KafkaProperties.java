package com.aston.frontendpracticeservice.kafka.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaProperties {

    private String bootstrapServers;
    private String topic;
    private Ssl ssl;
    private Producer producer;
    private Consumer consumer;

    @Data
    public static class Ssl {
        private String keyStorePassword;
        private String trustStorePassword;
        private String keyPassword;
    }

    @Data
    public static class Producer {
        private String keySerializer;
        private String valueSerializer;
        private Map<String, Object> properties;
    }

    @Data
    public static class Consumer {
        private String groupId;
        private String keyDeserializer;
        private String valueDeserializer;
        private String autoOffsetReset;
        private Map<String, Object> properties;
    }

}