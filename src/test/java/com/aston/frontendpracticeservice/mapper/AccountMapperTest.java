package com.aston.frontendpracticeservice.mapper;

import com.aston.frontendpracticeservice.controller.dto.AccountDto;
import com.aston.frontendpracticeservice.domain.entity.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountMapperTest {

    private final AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);

    @Test
    @DisplayName("Mapping from Account to AccountDto")
    void toDto_shouldMapAllFields_whenValidAccountProvided() {
        Account account = Account.builder()
                .accNumber("ACC123456")
                .money(new BigDecimal("1000.00"))
                .blocked(false)
                .build();

        AccountDto dto = accountMapper.toDto(account);

        assertThat(dto.accNumber()).isEqualTo(account.getAccNumber());
        assertThat(dto.money()).isEqualTo(account.getMoney());
        assertThat(dto.blocked()).isEqualTo(account.isBlocked());
    }

    @Test
    @DisplayName("Mapping from AccountDto to Account")
    void fromDto_shouldMapAllFields_whenValidDtoProvided() {
        AccountDto dto = new AccountDto("ACC654321", new BigDecimal("500.00"), true);

        Account account = accountMapper.fromDto(dto);

        assertThat(account.getAccNumber()).isEqualTo(dto.accNumber());
        assertThat(account.getMoney()).isEqualTo(dto.money());
        assertThat(account.isBlocked()).isEqualTo(dto.blocked());
    }

}
