package com.aston.frontendpracticeservice.kafka.service;

import com.aston.frontendpracticeservice.kafka.dto.KafkaMessageDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProducerServiceTest {

    private KafkaTemplate<String, KafkaMessageDto> kafkaTemplate;
    private ProducerService producerService;

    @BeforeEach
    void setUp() {
        kafkaTemplate = mock(KafkaTemplate.class);
        producerService = new ProducerService(kafkaTemplate);
        ReflectionTestUtils.setField(producerService, "topic", "test-topic");
    }

    @Test
    void sendMessage_shouldSendToKafkaTemplate() {
        KafkaMessageDto message = new KafkaMessageDto(UUID.randomUUID(), "test");
        org.springframework.test.util.ReflectionTestUtils.setField(producerService, "topic", "test-topic");
        producerService.sendMessage(message);
        ArgumentCaptor<KafkaMessageDto> captor = ArgumentCaptor.forClass(KafkaMessageDto.class);
        verify(kafkaTemplate, times(1)).send(eq("test-topic"), captor.capture());
        assertEquals(message, captor.getValue());
    }

}
