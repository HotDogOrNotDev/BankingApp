package com.aston.frontendpracticeservice.kafka.service;

import com.aston.frontendpracticeservice.kafka.dto.KafkaMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProducerService {

    private final KafkaTemplate<String, KafkaMessageDto> kafkaTemplate;

    @Value("${spring.kafka.topic}")
    private String topic;

    public void sendMessage(KafkaMessageDto message) {
        log.info("Sending message to kafka topic='{}': {}", topic, message);
        kafkaTemplate.send(topic, message);
    }

}
