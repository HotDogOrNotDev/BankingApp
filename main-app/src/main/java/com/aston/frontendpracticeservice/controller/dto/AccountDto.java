package com.aston.frontendpracticeservice.controller.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AccountDto(
        String accNumber,
        BigDecimal money,
        boolean blocked
) {
}
