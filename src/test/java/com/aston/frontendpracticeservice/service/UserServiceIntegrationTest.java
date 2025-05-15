package com.aston.frontendpracticeservice.service;

import com.aston.frontendpracticeservice.config.TestContainerConfig;
import com.aston.frontendpracticeservice.domain.entity.User;
import com.aston.frontendpracticeservice.domain.entity.UserRole;
import com.aston.frontendpracticeservice.exception.UserNotFoundException;
import com.aston.frontendpracticeservice.repository.UserRepository;
import com.aston.frontendpracticeservice.security.Role;
import org.junit.jupiter.api.BeforeEach;
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

        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .inn("123456789012")
                .snils("12345678901")
                .passportNumber("123456789")
                .login("johndoe")
                .password("password123")
                .build();

        UserRole userRole = new UserRole();
        userRole.setRole(Role.USER);
        userRole.setUser(user);

        user.setUserRoles(Set.of(userRole));

        userRepository.save(user);
    }

    @Test
    public void findUserByLogin_ShouldReturnUser_When_LoginExists() {
        User user = userService.findByLogin("johndoe");
        assertEquals("John", user.getFirstName());
    }

    @Test
    public void findUserByLogin_ShouldThrowException_When_LoginDoesNotExist() {
        assertThrows(UserNotFoundException.class, () -> userService.findByLogin("nonexistent"));
    }

    @Test
    public void findByLogin_ShouldReturnUserWithCorrectRole_When_LoginExists() {
        User user = userService.findByLogin("johndoe");

        Set<Role> roles = user.getUserRoles().stream()
                .map(UserRole::getRole)
                .collect(Collectors.toSet());

        assertEquals(Set.of(Role.USER), roles);
    }

}