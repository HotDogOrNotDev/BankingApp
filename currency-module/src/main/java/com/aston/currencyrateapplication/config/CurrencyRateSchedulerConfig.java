package com.aston.currencyrateapplication.config;

import com.aston.currencyrateapplication.service.CurrencyService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import java.time.Duration;
import java.time.LocalDate;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CurrencyRateSchedulerConfig {

    private final CurrencyService currencyService;

    @Value("${currency.rate.update-period:PT12H}")
    private Duration updatePeriod;

    Flux<Long> currencyRateUpdateFlux() {
        return Flux.interval(Duration.ZERO, updatePeriod);
    }

    @PostConstruct
    public void scheduleCurrencyRateUpdate() {
        currencyRateUpdateFlux()
                .concatMap(tick -> {
                    log.info("Старт обновления курсов: {}", LocalDate.now());
                    return currencyService.fetchAndSaveRates(LocalDate.now())
                            .doOnComplete(() -> log.info("Курсы обновлены: {}", LocalDate.now()))
                            .onErrorResume(e -> {
                                log.error("Ошибка при обновлении курсов", e);
                                return Flux.empty();
                            });
                })
                .subscribe();
    }

}
