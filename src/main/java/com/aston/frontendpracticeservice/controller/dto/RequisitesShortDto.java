package com.aston.frontendpracticeservice.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RequisitesShortDto (
       String accountNumber,
       String kbk,
       String userName
) {
}
