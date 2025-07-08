package com.aston.frontendpracticeservice.mapper;

import com.aston.frontendpracticeservice.controller.dto.AccountDto;
import com.aston.frontendpracticeservice.domain.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountMapper {

    @Mapping(target = "accNumber", source = "accNumber")
    AccountDto toDto(Account account);

    Account fromDto(AccountDto dto);

}
