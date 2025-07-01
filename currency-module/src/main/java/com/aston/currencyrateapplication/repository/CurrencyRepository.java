package com.aston.currencyrateapplication.repository;

import com.aston.currencyrateapplication.domain.entity.CurrencyRate;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

public interface CurrencyRepository extends R2dbcRepository<CurrencyRate, UUID> {

    Mono<CurrencyRate> findByDateAndCharCode(LocalDate date, String charCode);

    Flux<CurrencyRate> findAllByDate(LocalDate date);

}