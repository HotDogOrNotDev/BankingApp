package com.aston.frontendpracticeservice.service.impl;

import com.aston.frontendpracticeservice.domain.entity.Transfer;
import com.aston.frontendpracticeservice.repository.TransferRepository;
import com.aston.frontendpracticeservice.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Transfer createTransfer(String fromAcc, String toAcc, BigDecimal amount,
                                   boolean fraudChecked, boolean fraudDetected) {
        Transfer transfer = Transfer.builder()
                .fromAcc(fromAcc)
                .toAcc(toAcc)
                .amount(amount)
                .transferTime(LocalDateTime.now())
                .fraudChecked(fraudChecked)
                .fraudDetected(fraudDetected)
                .build();
        return transferRepository.save(transfer);
    }

}