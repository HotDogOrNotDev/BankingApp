package com.aston.frontendpracticeservice.service.impl;

import com.aston.frontendpracticeservice.controller.dto.AccountDto;
import com.aston.frontendpracticeservice.controller.dto.TransferRequestDto;
import com.aston.frontendpracticeservice.domain.entity.Account;
import com.aston.frontendpracticeservice.exception.AccountBlockedException;
import com.aston.frontendpracticeservice.exception.AccountNotFoundException;
import com.aston.frontendpracticeservice.exception.NotEnoughMoneyException;
import com.aston.frontendpracticeservice.mapper.AccountMapper;
import com.aston.frontendpracticeservice.repository.AccountRepository;
import com.aston.frontendpracticeservice.service.AccountService;
import com.aston.frontendpracticeservice.service.FraudDetectionService;
import com.aston.frontendpracticeservice.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountMapper accountMapper;
    private final AccountRepository accountRepository;
    private final TransferService transferService;
    private final FraudDetectionService fraudDetectionService;

    @Override
    @Transactional(readOnly = true)
    public AccountDto getBalance(String accNumber) {
        Account account = accountRepository.findById(accNumber)
                .orElseThrow(() -> new AccountNotFoundException(accNumber));
        return accountMapper.toDto(account);
    }

    @Override
    @Transactional
    public void transfer(TransferRequestDto request) throws InterruptedException {
        Account from = accountRepository.findById(request.fromAcc())
                .orElseThrow(() -> new AccountNotFoundException(request.fromAcc()));
        Account to = accountRepository.findById(request.toAcc())
                .orElseThrow(() -> new AccountNotFoundException(request.toAcc()));

        Object firstLock = from.getAccNumber().compareTo(to.getAccNumber()) < 0 ? from : to;
        Object secondLock = from.getAccNumber().compareTo(to.getAccNumber()) < 0 ? to : from;

        synchronized (firstLock) {
            synchronized (secondLock) {
                validateTransfer(from, to, request.amount());

                boolean fraudChecked = request.amount().compareTo(BigDecimal.valueOf(50000)) > 0;
                boolean fraudDetected = false;

                if (fraudChecked) {
                    fraudDetected = fraudDetectionService.checkForFraud(
                            from.getAccNumber(),
                            to.getAccNumber(),
                            request.amount()
                    );
                    if (fraudDetected) {
                        from.setBlocked(true);
                        to.setBlocked(true);
                    }
                }

                // Выполняем перевод
                from.setMoney(from.getMoney().subtract(request.amount()));
                to.setMoney(to.getMoney().add(request.amount()));

                // Сохраняем запись о переводе
                transferService.createTransfer(
                        from.getAccNumber(),
                        to.getAccNumber(),
                        request.amount(),
                        fraudChecked,
                        fraudDetected
                );

                accountRepository.save(from);
                accountRepository.save(to);
            }
        }
    }

    private void validateTransfer(Account from, Account to, BigDecimal amount) {
        if (from.isBlocked() || to.isBlocked()) {
            throw new AccountBlockedException();
        }
        if (from.getMoney().compareTo(amount) < 0) {
            throw new NotEnoughMoneyException();
        }
    }
}
