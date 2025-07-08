package com.aston.currencyrateapplication.controller;

import com.aston.currencyrateapplication.domain.entity.CurrencyRate;
import com.aston.currencyrateapplication.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/currency")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping("/rates")
    public Flux<CurrencyRate> getRatesByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return currencyService.getRatesByDate(date);
    }

    @PostMapping("/rates/update")
    public Flux<CurrencyRate> updateRates(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return currencyService.fetchAndSaveRates(date);
    }

}
