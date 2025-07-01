package com.aston.frontendpracticeservice.service.impl;

import com.aston.frontendpracticeservice.service.FraudDetectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class FraudDetectionServiceImpl implements FraudDetectionService {

    private final Object fraudLock = new Object();

    @Override
    public boolean checkForFraud(String fromAcc, String toAcc, BigDecimal amount)
            throws InterruptedException {
        synchronized (fraudLock) {
            Thread.sleep(1000);
            return Math.random() < 0.1;
        }
    }

}
