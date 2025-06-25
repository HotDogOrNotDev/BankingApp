package com.aston.frontendpracticeservice.kafka.service;

import com.aston.frontendpracticeservice.kafka.dto.KafkaMessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ConsumerService {

    @KafkaListener(topics = "${spring.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(KafkaMessageDto message) {
        log.info("Received message from kafka: {}", message);
    }

}
