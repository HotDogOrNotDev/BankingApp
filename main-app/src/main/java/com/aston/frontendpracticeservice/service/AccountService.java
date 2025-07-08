package com.aston.frontendpracticeservice.service;

import com.aston.frontendpracticeservice.controller.dto.AccountDto;
import com.aston.frontendpracticeservice.controller.dto.TransferRequestDto;

public interface AccountService {

    AccountDto getBalance(String accNumber);

    void transfer(TransferRequestDto request) throws InterruptedException;

}
