package com.aston.frontendpracticeservice.controller;

import com.aston.frontendpracticeservice.config.TestContainerConfig;
import com.aston.frontendpracticeservice.controller.dto.RequisitesCreateDto;
import com.aston.frontendpracticeservice.domain.entity.Account;
import com.aston.frontendpracticeservice.domain.entity.Requisites;
import com.aston.frontendpracticeservice.domain.entity.User;
import com.aston.frontendpracticeservice.repository.AccountRepository;
import com.aston.frontendpracticeservice.repository.RequisitesRepository;
import com.aston.frontendpracticeservice.repository.UserRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.liquibase.enabled=false",
                "spring.cache.type=none"
        }
)
@AutoConfigureWebTestClient
public class RequisitesControllerIntegrationTest extends TestContainerConfig {

    private static final BigDecimal DEFAULT_ACCOUNT_BALANCE = new BigDecimal("100000.00");
    private static final String DEFAULT_BIK = "044525225";
    private static final String DEFAULT_CORRESPONDENT_ACCOUNT = "30101810400000000225";
    private static final String DEFAULT_INN = "1234567890";
    private static final String DEFAULT_KPP = "123456789";
    private static final String DEFAULT_KBK = "123456789012345";

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequisitesRepository requisitesRepository;

    @Autowired
    private AccountRepository accountRepository;

    private User savedUser;
    private Requisites savedRequisites;
    private String savedAccountNumber;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @BeforeEach
    void setUp() {
        requisitesRepository.deleteAll();
        accountRepository.deleteAll();
        userRepository.deleteAll();
        savedUser = createDefaultUser();
        userRepository.save(savedUser);
        savedAccountNumber = "ACC" + UUID.randomUUID().toString().replace("-", "").substring(0, 17);
        savedRequisites = createDefaultRequisites(savedUser, savedAccountNumber);
        requisitesRepository.save(savedRequisites);
        requisitesRepository.flush();
    }

    @Test
    @DisplayName("Returns only userName, accountNumber, kbk")
    void getRequisites_shouldReturnOnlyRequiredFields_whenRequested() {
        System.out.println("JPQL result: " + requisitesRepository.findAllBy());
        webTestClient.get().uri("/api/requisites")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$[0].accountNumber").isEqualTo(savedAccountNumber)
                .jsonPath("$[0].kbk").isEqualTo(savedRequisites.getKbk())
                .jsonPath("$[0].userName").isEqualTo(savedUser.getFirstName())
                .jsonPath("$[0].bik").doesNotExist()
                .jsonPath("$[0].inn").doesNotExist()
                .jsonPath("$[0].kpp").doesNotExist();
    }

    @Test
    @DisplayName("Should create new requisites with valid data")
    void createRequisite_shouldCreateNewRequisites_whenValidDataProvided() {
        User user = userRepository.save(createDefaultUser());
        String accNumber = UUID.randomUUID().toString();
        Account account = createDefaultAccount(accNumber);
        accountRepository.save(account);

        RequisitesCreateDto createDto = new RequisitesCreateDto(
                accNumber,
                DEFAULT_BIK,
                DEFAULT_CORRESPONDENT_ACCOUNT,
                DEFAULT_INN,
                DEFAULT_KPP,
                DEFAULT_KBK,
                user.getId()
        );

        webTestClient.post().uri("/api/requisites")
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.accountNumber").isEqualTo(createDto.accountNumber())
                .jsonPath("$.kbk").isEqualTo(createDto.kbk())
                .jsonPath("$.userName").isEqualTo(user.getFirstName());
    }

    @Test
    @DisplayName("Should delete requisites by id")
    void deleteRequisite_shouldDeleteRequisites_whenValidIdProvided() {
        webTestClient.delete().uri("/api/requisites/" + savedRequisites.getId())
                .exchange()
                .expectStatus().isNoContent();

        webTestClient.get().uri("/api/requisites")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$").isEmpty();
    }

    private User createDefaultUser() {
        return User.builder()
                .firstName("Test")
                .lastName("User")
                .birthDate(LocalDate.of(1990, 1, 1))
                .inn("123456789012")
                .snils("12345678901")
                .passportNumber("123456789")
                .login("testuser")
                .password("password")
                .build();
    }

    private Account createDefaultAccount(String accNumber) {
        return Account.builder()
                .accNumber(accNumber)
                .money(DEFAULT_ACCOUNT_BALANCE)
                .blocked(false)
                .build();
    }

    private Requisites createDefaultRequisites(User user, String accNumber) {
        Account account = createDefaultAccount(accNumber);
        accountRepository.save(account);

        return Requisites.builder()
                .account(account)
                .bik(DEFAULT_BIK)
                .correspondentAccount(DEFAULT_CORRESPONDENT_ACCOUNT)
                .inn(DEFAULT_INN)
                .kpp(DEFAULT_KPP)
                .kbk(DEFAULT_KBK)
                .user(user)
                .build();
    }

}