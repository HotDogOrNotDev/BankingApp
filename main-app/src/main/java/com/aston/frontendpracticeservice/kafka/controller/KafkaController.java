package com.aston.frontendpracticeservice.kafka.controller;

import com.aston.frontendpracticeservice.kafka.dto.KafkaMessageDto;
import com.aston.frontendpracticeservice.kafka.service.ProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/kafka")
@RequiredArgsConstructor
public class KafkaController {

    private final ProducerService producerService;

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody KafkaMessageDto message) {
        producerService.sendMessage(message);
        return ResponseEntity.ok("Message sent to Kafka topic!");
    }

}
