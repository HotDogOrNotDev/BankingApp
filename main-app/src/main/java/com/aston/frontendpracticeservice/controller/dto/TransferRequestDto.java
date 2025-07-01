package com.aston.frontendpracticeservice.controller.dto;

import java.math.BigDecimal;

public record TransferRequestDto(
        String fromAcc,
        String toAcc,
        BigDecimal amount) {
}