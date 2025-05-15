package com.aston.frontendpracticeservice.domain.entity;

import com.aston.frontendpracticeservice.security.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.aston.frontendpracticeservice.domain.util.RegexPatterns.INN_PATTERN;
import static com.aston.frontendpracticeservice.domain.util.RegexPatterns.PASSPORT_PATTERN;
import static com.aston.frontendpracticeservice.domain.util.RegexPatterns.SNILS_PATTERN;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Size(min = 1, max = 50)
    private String firstName;

    @NotNull
    @Size(min = 1, max = 50)
    private String lastName;

    @NotNull
    private LocalDate birthDate;

    @NotNull
    @Pattern(regexp = INN_PATTERN , message = "ИНН должен содержать от 10 до 12 цифр")
    private String inn;

    @NotNull
    @Pattern(regexp = SNILS_PATTERN, message = "СНИЛС должен содержать 11 цифр")
    private String snils;

    @NotNull
    @Pattern(regexp = PASSPORT_PATTERN, message = "Номер паспорта должен содержать 9 цифр")
    private String passportNumber;

    @NotNull
    @Size(min = 5, max = 50)
    private String login;

    @NotNull
    @Size(min = 8, max = 100)
    private String password;

    @OneToMany(mappedBy = "user",fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRole> userRoles = new HashSet<>();

}
