package com.aston.frontendpracticeservice.controller.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record RequisitesCreateDto(
        String accountNumber,
        String bik,
        String correspondentAccount,
        String inn,
        String kpp,
        String kbk,
        UUID userId
) {
}
