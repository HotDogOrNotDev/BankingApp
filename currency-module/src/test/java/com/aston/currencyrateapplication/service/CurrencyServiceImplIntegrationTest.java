package com.aston.currencyrateapplication.service;

import com.aston.currencyrateapplication.config.TestContainerConfig;
import com.aston.currencyrateapplication.domain.entity.CurrencyRate;
import com.aston.currencyrateapplication.repository.CurrencyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class CurrencyServiceImplIntegrationTest extends TestContainerConfig {

    @Autowired
    private CurrencyService currencyService;

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
    @DisplayName("fetchAndSaveRates сохраняет курсы в базу")
    void fetchAndSaveRates_shouldSaveRates() {
        LocalDate testDate = LocalDate.of(2025, 7, 1);

        Flux<CurrencyRate> savedRates = currencyService.fetchAndSaveRates(testDate);
        assertThat(savedRates.collectList().block()).isNotEmpty();

        Flux<CurrencyRate> allRates = currencyRepository.findAllByDate(testDate);
        assertThat(allRates.collectList().block()).isNotEmpty();
    }

    @Test
    @DisplayName("getRatesByDate возвращает курсы из базы")
    void getRatesByDate_shouldReturnRates() {
        LocalDate testDate = LocalDate.of(2025, 7, 1);

        currencyService.fetchAndSaveRates(testDate).collectList().block();

        Flux<CurrencyRate> rates = currencyService.getRatesByDate(testDate);
        assertThat(rates.collectList().block()).isNotEmpty();
    }

}
