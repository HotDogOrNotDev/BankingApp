package com.aston.frontendpracticeservice.kafka.config;

import com.aston.frontendpracticeservice.kafka.config.properties.KafkaProperties;
import com.aston.frontendpracticeservice.kafka.dto.KafkaMessageDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    public ConsumerFactory<String, KafkaMessageDto> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getConsumer().getGroupId());
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getConsumer().getKeyDeserializer());
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getConsumer().getValueDeserializer());
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaProperties.getConsumer().getAutoOffsetReset());

        configProps.put("security.protocol", "SSL");
        configProps.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, "ssl/kafka.keystore.jks");
        configProps.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, kafkaProperties.getSsl().getKeyStorePassword());
        configProps.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "ssl/kafka.truststore.jks");
        configProps.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, kafkaProperties.getSsl().getTrustStorePassword());
        configProps.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, kafkaProperties.getSsl().getKeyPassword());

        if (kafkaProperties.getConsumer().getProperties() != null) {
            configProps.putAll(kafkaProperties.getConsumer().getProperties());
        }

        return new DefaultKafkaConsumerFactory<>(
                configProps,
                new org.apache.kafka.common.serialization.StringDeserializer(),
                new JsonDeserializer<>(KafkaMessageDto.class)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaMessageDto> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, KafkaMessageDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

}
