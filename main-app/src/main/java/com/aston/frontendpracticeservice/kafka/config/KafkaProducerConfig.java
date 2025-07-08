package com.aston.frontendpracticeservice.kafka.config;

import com.aston.frontendpracticeservice.kafka.config.properties.KafkaProperties;
import com.aston.frontendpracticeservice.kafka.dto.KafkaMessageDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    public ProducerFactory<String, KafkaMessageDto> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaProperties.getProducer().getKeySerializer());
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaProperties.getProducer().getValueSerializer());

        configProps.put("security.protocol", "SSL");
        configProps.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, "ssl/kafka.keystore.jks");
        configProps.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, kafkaProperties.getSsl().getKeyStorePassword());
        configProps.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "ssl/kafka.truststore.jks");
        configProps.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, kafkaProperties.getSsl().getTrustStorePassword());
        configProps.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, kafkaProperties.getSsl().getKeyPassword());

        if (kafkaProperties.getProducer().getProperties() != null) {
            configProps.putAll(kafkaProperties.getProducer().getProperties());
        }

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, KafkaMessageDto> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}