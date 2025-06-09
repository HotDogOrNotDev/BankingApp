package com.aston.frontendpracticeservice.controller;

import com.aston.frontendpracticeservice.config.TestContainerConfig;
import com.aston.frontendpracticeservice.controller.dto.RequisitesCreateDto;
import com.aston.frontendpracticeservice.domain.entity.Requisites;
import com.aston.frontendpracticeservice.domain.entity.User;
import com.aston.frontendpracticeservice.repository.RequisitesRepository;
import com.aston.frontendpracticeservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.liquibase.enabled=false")
@AutoConfigureWebTestClient
public class RequisitesControllerIntegrationTest extends TestContainerConfig {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequisitesRepository requisitesRepository;

    private User savedUser;
    private Requisites savedRequisites;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @BeforeEach
    void setUp() {
        requisitesRepository.deleteAll();
        userRepository.deleteAll();
        savedUser = createDefaultUser();
        userRepository.save(savedUser);
        savedRequisites = createDefaultRequisites();
        savedRequisites.setUser(savedUser);
        requisitesRepository.save(savedRequisites);
    }

    @Test
    @DisplayName("Returns only userName, accountNumber, kbk")
    void getRequisites_shouldReturnOnlyRequiredFields_whenRequested() {
        webTestClient.get().uri("/api/requisites")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$[0].userName").isEqualTo(savedUser.getFirstName())
                .jsonPath("$[0].accountNumber").isEqualTo(savedRequisites.getAccountNumber())
                .jsonPath("$[0].kbk").isEqualTo(savedRequisites.getKbk())
                .jsonPath("$[0].bik").doesNotExist()
                .jsonPath("$[0].inn").doesNotExist()
                .jsonPath("$[0].kpp").doesNotExist();
    }

    @Test
    @DisplayName("Should create new requisites with valid data")
    void createRequisite_shouldCreateNewRequisites_whenValidDataProvided() {
        User user = userRepository.save(createDefaultUser());
        RequisitesCreateDto createDto = RequisitesCreateDto.builder()
                .accountNumber("1234567890")
                .bik("044525225")
                .correspondentAccount("30101810400000000225")
                .inn("1234567890")
                .kpp("123456789")
                .kbk("123456789012345")
                .userId(user.getId())
                .build();

        webTestClient.post().uri("/api/requisites")
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.accountNumber").isEqualTo(createDto.accountNumber())
                .jsonPath("$.kbk").isEqualTo(createDto.kbk())
                .jsonPath("$.userName").isEqualTo(savedUser.getFirstName());
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

    private Requisites createDefaultRequisites() {
        return Requisites.builder()
                .accountNumber("1234567890")
                .bik("044525225")
                .correspondentAccount("30101810400000000225")
                .inn("1234567890")
                .kpp("123456789")
                .kbk("123456789012345")
                .build();
    }

}