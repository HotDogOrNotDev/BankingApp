package com.aston.frontendpracticeservice.kafka.controller;

import com.aston.frontendpracticeservice.kafka.dto.KafkaMessageDto;
import com.aston.frontendpracticeservice.kafka.service.ProducerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class KafkaControllerTest {

    @Mock
    private ProducerService producerService;

    @InjectMocks
    private KafkaController kafkaController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(kafkaController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void sendMessage_whenValidMessage_thenReturnsOk() throws Exception {
        KafkaMessageDto messageDto = new KafkaMessageDto(
                UUID.randomUUID(),
                "test message from controller"
        );

        doNothing().when(producerService).sendMessage(any(KafkaMessageDto.class));

        mockMvc.perform(post("/api/v1/kafka/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(messageDto)))
                .andExpect(status().isOk());
    }

}
