package com.aston.frontendpracticeservice.service;

import com.aston.frontendpracticeservice.service.impl.FraudDetectionServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class FraudDetectionServiceImplTest {

    private final FraudDetectionService fraudDetectionService = new FraudDetectionServiceImpl();

    @Test
    @DisplayName("checkForFraud returns boolean and waits ~1 second")
    void checkForFraud_shouldReturnBooleanAndBeSynchronized() throws InterruptedException {
        long start = System.currentTimeMillis();
        boolean result = fraudDetectionService.checkForFraud("ACC1", "ACC2", new BigDecimal("100.00"));
        long duration = System.currentTimeMillis() - start;

        assertThat(result).isInstanceOf(Boolean.class);
        assertThat(duration).isGreaterThanOrEqualTo(950); // допускаем небольшую погрешность
    }

}
