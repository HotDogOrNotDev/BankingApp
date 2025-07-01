package com.aston.currencyrateapplication.controller;

import com.aston.currencyrateapplication.config.TestContainerConfig;
import com.aston.currencyrateapplication.domain.entity.CurrencyRate;
import com.aston.currencyrateapplication.repository.CurrencyRepository;
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
import reactor.core.publisher.Flux;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureWebTestClient
@Testcontainers
public class CurrencyControllerIntegrationTest extends TestContainerConfig {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private CurrencyRepository currencyRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> "r2dbc:postgresql://"
                + postgreSQLContainer.getHost() + ":"
                + postgreSQLContainer.getMappedPort(5432) + "/"
                + postgreSQLContainer.getDatabaseName());
        registry.add("spring.r2dbc.username", postgreSQLContainer::getUsername);
        registry.add("spring.r2dbc.password", postgreSQLContainer::getPassword);

        // Для Liquibase нужен JDBC URL!
        registry.add("spring.liquibase.url", () -> "jdbc:postgresql://"
                + postgreSQLContainer.getHost() + ":"
                + postgreSQLContainer.getMappedPort(5432) + "/"
                + postgreSQLContainer.getDatabaseName());
        registry.add("spring.liquibase.user", postgreSQLContainer::getUsername);
        registry.add("spring.liquibase.password", postgreSQLContainer::getPassword);
    }

    @BeforeEach
    void setUp() {
        currencyRepository.deleteAll().block();
    }

    @Test
    @DisplayName("POST /api/currency/rates/update загружает и сохраняет курсы")
    void updateRates_shouldFetchAndSaveRates() {
        LocalDate testDate = LocalDate.of(2025, 7, 1);

        // Запрос на обновление курсов
        webTestClient.post()
                .uri("/api/currency/rates/update?date={date}", testDate)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CurrencyRate.class)
                .value(rates -> {
                    assertThat(rates).isNotEmpty();
                    assertThat(rates.get(0).getDate()).isEqualTo(testDate);
                });

        Flux<CurrencyRate> allRates = currencyRepository.findAllByDate(testDate);
        assertThat(allRates.collectList().block()).isNotEmpty();
    }

    @Test
    @DisplayName("GET /api/currency/rates возвращает курсы за дату")
    void getRatesByDate_shouldReturnRates() {
        LocalDate testDate = LocalDate.of(2025, 7, 1);

        webTestClient.post()
                .uri("/api/currency/rates/update?date={date}", testDate)
                .exchange()
                .expectStatus().isOk();

        webTestClient.get()
                .uri("/api/currency/rates?date={date}", testDate)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CurrencyRate.class)
                .value(rates -> {
                    assertThat(rates).isNotEmpty();
                    assertThat(rates.get(0).getDate()).isEqualTo(testDate);
                });
    }

}