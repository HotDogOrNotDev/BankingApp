package com.aston.frontendpracticeservice.kafka.service;

import com.aston.frontendpracticeservice.kafka.dto.KafkaMessageDto;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ConsumerServiceTest {

    @Test
    void listen_shouldLogMessage() {
        LogCaptor logCaptor = LogCaptor.forClass(ConsumerService.class);
        ConsumerService consumerService = new ConsumerService();
        KafkaMessageDto message = new KafkaMessageDto(UUID.randomUUID(), "test");
        consumerService.listen(message);
        assertThat(logCaptor.getInfoLogs())
                .anyMatch(log -> log.contains("Received message from kafka"));
    }

}