package com.aston.frontendpracticeservice.service;

import java.math.BigDecimal;

public interface FraudDetectionService {

    boolean checkForFraud(String fromAcc, String toAcc, BigDecimal amount)
            throws InterruptedException;

}
