package com.aston.frontendpracticeservice.controller;

import com.aston.frontendpracticeservice.config.TestContainerConfig;
import com.aston.frontendpracticeservice.domain.entity.Requisites;
import com.aston.frontendpracticeservice.domain.entity.User;
import com.aston.frontendpracticeservice.repository.RequisitesRepository;
import com.aston.frontendpracticeservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.liquibase.enabled=false")
@AutoConfigureWebTestClient
public class RequisitesControllerIntegrationTest extends TestContainerConfig {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequisitesRepository requisitesRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        User user = User.builder()
                .firstName("Ivan")
                .lastName("Ivanov")
                .birthDate(LocalDate.now())
                .inn("1234567890")
                .snils("12345678901")
                .passportNumber("123456789")
                .login("ivan")
                .password("password")
                .build();

        userRepository.save(user);

        Requisites requisites = new Requisites();
        requisites.setId(null);
        requisites.setAccountNumber("12345678901234567890");
        requisites.setBik("BIK123456");
        requisites.setCorrespondentAccount("CORR1234567890123456");
        requisites.setInn("1234567890");
        requisites.setKpp("KPP1234567");
        requisites.setKbk("KBK123");
        requisites.setUser(user);

        requisitesRepository.save(requisites);
    }

    @Test
    void shouldReturnOnlyRequiredFields() {
        webTestClient.get().uri("/api/requisites")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$[0].userName").isEqualTo("Ivan")
                .jsonPath("$[0].accountNumber").isEqualTo("12345678901234567890")
                .jsonPath("$[0].kbk").isEqualTo("KBK123")
                .jsonPath("$[0].bik").doesNotExist()
                .jsonPath("$[0].inn").doesNotExist()
                .jsonPath("$[0].kpp").doesNotExist();
    }

}