package com.aston.frontendpracticeservice.service;

import com.aston.frontendpracticeservice.config.TestContainerConfig;
import com.aston.frontendpracticeservice.domain.entity.User;
import com.aston.frontendpracticeservice.domain.entity.UserRole;
import com.aston.frontendpracticeservice.exception.UserNotFoundException;
import com.aston.frontendpracticeservice.repository.UserRepository;
import com.aston.frontendpracticeservice.security.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
public class UserServiceIntegrationTest extends TestContainerConfig {

    public static final String LOGIN = "johndoe";
    public static final String EXPECTED_FIRST_NAME = "John";
    public static final String NONEXISTENT_LOGIN = "nonexistent";

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        User user = createDefaultUser();
        UserRole userRole = createDefaultUserRole(user);
        user.setUserRoles(Set.of(userRole));
        userRepository.save(user);
    }

    @Test
    @DisplayName("Finds user by login")
    public void findUserByLogin_ShouldReturnUser_When_LoginExists() {
        User user = userService.findByLogin(LOGIN);
        assertEquals(EXPECTED_FIRST_NAME, user.getFirstName());
    }

    @Test
    @DisplayName("Throws UserNotFoundException when User not found ")
    public void findUserByLogin_ShouldThrowException_When_LoginDoesNotExist() {
        assertThrows(UserNotFoundException.class, () -> userService.findByLogin(NONEXISTENT_LOGIN));
    }

    @Test
    @DisplayName("Finds user by login with correct role")
    public void findByLogin_ShouldReturnUserWithCorrectRole_When_LoginExists() {
        User user = userService.findByLogin(LOGIN);

        Set<Role> roles = user.getUserRoles().stream()
                .map(UserRole::getRole)
                .collect(Collectors.toSet());

        assertEquals(Set.of(Role.USER), roles);
    }

    private User createDefaultUser() {
        return User.builder()
                .firstName(EXPECTED_FIRST_NAME)
                .lastName("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .inn("123456789012")
                .snils("12345678901")
                .passportNumber("123456789")
                .login(LOGIN)
                .password("password123")
                .build();
    }

    private UserRole createDefaultUserRole(User user) {
        UserRole userRole = new UserRole();
        userRole.setRole(Role.USER);
        userRole.setUser(user);
        return userRole;
    }
}