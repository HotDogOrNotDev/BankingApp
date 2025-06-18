package com.aston.frontendpracticeservice.service;

import com.aston.frontendpracticeservice.config.TestContainerConfig;
import com.aston.frontendpracticeservice.controller.dto.RequisitesCreateDto;
import com.aston.frontendpracticeservice.controller.dto.RequisitesShortDto;
import com.aston.frontendpracticeservice.domain.entity.Account;
import com.aston.frontendpracticeservice.domain.entity.Requisites;
import com.aston.frontendpracticeservice.domain.entity.User;
import com.aston.frontendpracticeservice.exception.UserNotFoundException;
import com.aston.frontendpracticeservice.repository.AccountRepository;
import com.aston.frontendpracticeservice.repository.RequisitesRepository;
import com.aston.frontendpracticeservice.repository.TransferRepository;
import com.aston.frontendpracticeservice.repository.UserRepository;
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
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(
        properties = {
                "spring.cache.type=none"
        }
)
@Testcontainers
public class RequisitesServiceIntegrationTest extends TestContainerConfig {

    @Autowired
    private RequisitesService requisitesService;

    @Autowired
    private TransferRepository transferRepository;

    @Autowired
    private RequisitesRepository requisitesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    private static final String DEFAULT_BIK = "044525225";
    private static final String DEFAULT_CORRESPONDENT_ACCOUNT = "30101810400000000225";
    private static final String DEFAULT_INN = "1234567890";
    private static final String DEFAULT_KPP = "123456789";
    private static final String DEFAULT_KBK = "123456789012345";
    private static final BigDecimal DEFAULT_ACCOUNT_BALANCE = new BigDecimal("100000.00");

    private User savedUser;
    private Account savedAccount;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @BeforeEach
    void setUp() {
        transferRepository.deleteAll();
        requisitesRepository.deleteAll();
        accountRepository.deleteAll();
        userRepository.deleteAll();
        savedUser = userRepository.save(createDefaultUser());
        savedAccount = accountRepository.save(createDefaultAccount("ACC" + UUID.randomUUID().toString().replace("-", "").substring(0, 17)));
    }

    @Test
    @DisplayName("getAllRequisites returns correct data")
    void getAllRequisites_shouldReturnAll() {
        Requisites requisites = createDefaultRequisites(savedUser, savedAccount);
        requisitesRepository.save(requisites);

        List<RequisitesShortDto> result = requisitesService.getAllRequisites();
        assertEquals(1, result.size());
        RequisitesShortDto dto = result.get(0);
        assertEquals(savedAccount.getAccNumber(), dto.accountNumber());
        assertEquals(DEFAULT_KBK, dto.kbk());
        assertEquals(savedUser.getFirstName(), dto.userName());
    }

    @Test
    @DisplayName("create creates new requisites")
    void create_shouldCreateNewRequisites() {
        RequisitesCreateDto dto = new RequisitesCreateDto(
                savedAccount.getAccNumber(),
                DEFAULT_BIK,
                DEFAULT_CORRESPONDENT_ACCOUNT,
                DEFAULT_INN,
                DEFAULT_KPP,
                DEFAULT_KBK,
                savedUser.getId()
        );
        RequisitesShortDto created = requisitesService.create(dto);

        assertEquals(savedAccount.getAccNumber(), created.accountNumber());
        assertEquals(DEFAULT_KBK, created.kbk());
        assertEquals(savedUser.getFirstName(), created.userName());
        assertEquals(1, requisitesRepository.count());
    }

    @Test
    @DisplayName("create throws UserNotFoundException for invalid user")
    void create_shouldThrowException_whenUserNotFound() {
        RequisitesCreateDto dto = new RequisitesCreateDto(
                savedAccount.getAccNumber(),
                DEFAULT_BIK,
                DEFAULT_CORRESPONDENT_ACCOUNT,
                DEFAULT_INN,
                DEFAULT_KPP,
                DEFAULT_KBK,
                UUID.randomUUID()
        );
        assertThrows(UserNotFoundException.class, () -> requisitesService.create(dto));
    }

    @Test
    @DisplayName("deleteRequisite removes requisites")
    void deleteRequisite_shouldDelete() {
        Requisites requisites = createDefaultRequisites(savedUser, savedAccount);
        requisites = requisitesRepository.save(requisites);

        requisitesService.deleteRequisite(requisites.getId());
        assertEquals(0, requisitesRepository.count());
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

    private Requisites createDefaultRequisites(User user, Account account) {
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
