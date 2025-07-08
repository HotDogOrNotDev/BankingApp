package com.aston.currencyrateapplication.config;

import com.aston.currencyrateapplication.service.CurrencyService;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CurrencyRateSchedulerConfigTest {

    @Test
    void schedulerShouldCallServicePeriodically() {
        VirtualTimeScheduler vts = VirtualTimeScheduler.create();

        CurrencyService mockService = mock(CurrencyService.class);
        when(mockService.fetchAndSaveRates(any(LocalDate.class)))
                .thenReturn(Flux.empty());

        Flux.interval(Duration.ZERO, Duration.ofHours(12), vts)
                .take(3)
                .concatMap(tick -> mockService.fetchAndSaveRates(LocalDate.now()))
                .subscribe();

        // Продвигаем виртуальное время на 36 часов (3 раза по 12)
        vts.advanceTimeBy(Duration.ofHours(36));

        verify(mockService, times(3)).fetchAndSaveRates(any(LocalDate.class));
    }

}
