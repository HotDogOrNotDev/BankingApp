package com.aston.frontendpracticeservice.handler.dto;

public record ErrorResponse(
        int code,
        String message
) {
}
