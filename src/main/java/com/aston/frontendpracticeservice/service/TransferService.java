package com.aston.frontendpracticeservice.service;

import com.aston.frontendpracticeservice.domain.entity.Transfer;

import java.math.BigDecimal;

public interface TransferService {

    Transfer createTransfer(String fromAcc, String toAcc, BigDecimal amount, boolean fraudChecked, boolean fraudDetected);

}
