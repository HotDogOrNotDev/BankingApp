package com.aston.currencyrateapplication.service;

import com.aston.currencyrateapplication.domain.entity.CurrencyRate;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

public interface CurrencyService {

    Flux<CurrencyRate> fetchAndSaveRates(LocalDate date);

    Flux<CurrencyRate> getRatesByDate(LocalDate date);

}
