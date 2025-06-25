package com.aston.frontendpracticeservice.kafka.dto;

import java.util.UUID;

public record KafkaMessageDto(
        UUID id,
        String message
) {
}
