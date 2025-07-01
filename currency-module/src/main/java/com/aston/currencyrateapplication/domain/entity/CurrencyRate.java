package com.aston.currencyrateapplication.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Table("currency_rate")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrencyRate {

    @Id
    private UUID id;
    private LocalDate date;
    private String cbrId;
    private String numCode;
    private String charCode;
    private Integer nominal;
    private String name;
    private BigDecimal value;
    private BigDecimal vunitRate;

}
