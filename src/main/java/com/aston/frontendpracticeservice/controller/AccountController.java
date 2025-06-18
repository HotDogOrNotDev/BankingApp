package com.aston.frontendpracticeservice.controller;

import com.aston.frontendpracticeservice.controller.dto.AccountDto;
import com.aston.frontendpracticeservice.controller.dto.TransferRequestDto;
import com.aston.frontendpracticeservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{accNumber}/balance")
    public AccountDto getBalance(@PathVariable String accNumber) {
        return accountService.getBalance(accNumber);
    }

    @PostMapping("/transfer")
    public void transfer(@RequestBody TransferRequestDto request) throws InterruptedException {
        accountService.transfer(request);
    }

}

