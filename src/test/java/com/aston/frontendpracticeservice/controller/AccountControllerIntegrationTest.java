package com.aston.frontendpracticeservice.controller;

import com.aston.frontendpracticeservice.config.TestContainerConfig;
import com.aston.frontendpracticeservice.controller.dto.AccountDto;
import com.aston.frontendpracticeservice.controller.dto.TransferRequestDto;
import com.aston.frontendpracticeservice.domain.entity.Account;
import com.aston.frontendpracticeservice.repository.AccountRepository;
import com.aston.frontendpracticeservice.repository.TransferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.cache.type=none"
        }
)
@AutoConfigureWebTestClient
@Testcontainers
public class AccountControllerIntegrationTest extends TestContainerConfig {

    @Autowired
    private WebTestClient webTestClient;

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
    @DisplayName("GET /api/accounts/{accNumber}/balance returns correct AccountDto")
    void getBalance_shouldReturnAccountDto() {
        webTestClient.get()
                .uri("/api/accounts/{accNumber}/balance", accountFrom.getAccNumber())
                .exchange()
                .expectStatus().isOk()
                .expectBody(AccountDto.class)
                .value(dto -> {
                    assertThat(dto.accNumber()).isEqualTo(accountFrom.getAccNumber());
                    assertThat(dto.money()).isEqualTo(accountFrom.getMoney());
                    assertThat(dto.blocked()).isEqualTo(accountFrom.isBlocked());
                });
    }

    @Test
    @DisplayName("POST /api/accounts/transfer moves money between accounts")
    void transfer_shouldMoveMoney() {
        BigDecimal amount = new BigDecimal("1000.00");
        TransferRequestDto request = new TransferRequestDto(accountFrom.getAccNumber(), accountTo.getAccNumber(), amount);

        webTestClient.post()
                .uri("/api/accounts/transfer")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();

        Account updatedFrom = accountRepository.findById(accountFrom.getAccNumber()).orElseThrow();
        Account updatedTo = accountRepository.findById(accountTo.getAccNumber()).orElseThrow();

        assertThat(updatedFrom.getMoney()).isEqualTo(DEFAULT_ACCOUNT_BALANCE.subtract(amount));
        assertThat(updatedTo.getMoney()).isEqualTo(DEFAULT_ACCOUNT_BALANCE.add(amount));
    }

    private Account createAccount(String accNumber, BigDecimal money, boolean blocked) {
        return Account.builder()
                .accNumber(accNumber)
                .money(money)
                .blocked(blocked)
                .build();
    }

}
