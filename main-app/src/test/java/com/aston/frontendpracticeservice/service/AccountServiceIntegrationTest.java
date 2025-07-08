package com.aston.frontendpracticeservice.service;

import com.aston.frontendpracticeservice.config.TestContainerConfig;
import com.aston.frontendpracticeservice.controller.dto.AccountDto;
import com.aston.frontendpracticeservice.controller.dto.TransferRequestDto;
import com.aston.frontendpracticeservice.domain.entity.Account;
import com.aston.frontendpracticeservice.exception.AccountBlockedException;
import com.aston.frontendpracticeservice.exception.AccountNotFoundException;
import com.aston.frontendpracticeservice.exception.NotEnoughMoneyException;
import com.aston.frontendpracticeservice.repository.AccountRepository;
import com.aston.frontendpracticeservice.repository.TransferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(
        properties = {
                "spring.cache.type=none"
        }
)
@Testcontainers
public class AccountServiceIntegrationTest extends TestContainerConfig {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransferRepository transferRepository;

    private static final BigDecimal DEFAULT_ACCOUNT_BALANCE = new BigDecimal("100000.00");

    private Account accountFrom;
    private Account accountTo;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @BeforeEach
    void setUp() {
        transferRepository.deleteAll();
        accountRepository.deleteAll();
        accountFrom = accountRepository.save(createAccount("ACC" + UUID.randomUUID().toString().replace("-", "").substring(0, 17), DEFAULT_ACCOUNT_BALANCE, false));
        accountTo = accountRepository.save(createAccount("ACC" + UUID.randomUUID().toString().replace("-", "").substring(0, 17), DEFAULT_ACCOUNT_BALANCE, false));
    }

    @Test
    @DisplayName("getBalance returns correct data")
    void getBalance_shouldReturnAccountDto() {
        AccountDto dto = accountService.getBalance(accountFrom.getAccNumber());
        assertEquals(accountFrom.getAccNumber(), dto.accNumber());
        assertEquals(accountFrom.getMoney(), dto.money());
        assertEquals(accountFrom.isBlocked(), dto.blocked());
    }

    @Test
    @DisplayName("getBalance throws AccountNotFoundException for invalid account")
    void getBalance_shouldThrowException_whenAccountNotFound() {
        assertThrows(AccountNotFoundException.class, () -> accountService.getBalance("NOT_EXIST"));
    }

    @Test
    @DisplayName("transfer moves money between accounts")
    void transfer_shouldMoveMoney() throws InterruptedException {
        BigDecimal amount = new BigDecimal("1000.00");
        TransferRequestDto request = new TransferRequestDto(accountFrom.getAccNumber(), accountTo.getAccNumber(), amount);

        accountService.transfer(request);

        Account updatedFrom = accountRepository.findById(accountFrom.getAccNumber()).orElseThrow();
        Account updatedTo = accountRepository.findById(accountTo.getAccNumber()).orElseThrow();

        assertEquals(DEFAULT_ACCOUNT_BALANCE.subtract(amount), updatedFrom.getMoney());
        assertEquals(DEFAULT_ACCOUNT_BALANCE.add(amount), updatedTo.getMoney());
    }

    @Test
    @DisplayName("transfer throws NotEnoughMoneyException if not enough funds")
    void transfer_shouldThrowException_whenNotEnoughMoney() {
        BigDecimal amount = new BigDecimal("1000000.00");
        TransferRequestDto request = new TransferRequestDto(accountFrom.getAccNumber(), accountTo.getAccNumber(), amount);

        assertThrows(NotEnoughMoneyException.class, () -> accountService.transfer(request));
    }

    @Test
    @DisplayName("transfer throws AccountBlockedException if account is blocked")
    void transfer_shouldThrowException_whenAccountBlocked() {
        accountFrom.setBlocked(true);
        accountRepository.save(accountFrom);

        TransferRequestDto request = new TransferRequestDto(accountFrom.getAccNumber(), accountTo.getAccNumber(), new BigDecimal("100.00"));

        assertThrows(AccountBlockedException.class, () -> accountService.transfer(request));
    }

    private Account createAccount(String accNumber, BigDecimal money, boolean blocked) {
        return Account.builder()
                .accNumber(accNumber)
                .money(money)
                .blocked(blocked)
                .build();
    }

}