package com.aston.frontendpracticeservice.mapper;

import com.aston.frontendpracticeservice.controller.dto.RequisitesCreateDto;
import com.aston.frontendpracticeservice.controller.dto.RequisitesShortDto;
import com.aston.frontendpracticeservice.domain.entity.Account;
import com.aston.frontendpracticeservice.domain.entity.Requisites;
import com.aston.frontendpracticeservice.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RequisitesMapper {

    @Mapping(target = "account", source = "dto.accountNumber")
    @Mapping(target = "bik", source = "dto.bik")
    @Mapping(target = "correspondentAccount", source = "dto.correspondentAccount")
    @Mapping(target = "inn", source = "dto.inn")
    @Mapping(target = "kpp", source = "dto.kpp")
    @Mapping(target = "kbk", source = "dto.kbk")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "id", ignore = true)
    Requisites toEntity(RequisitesCreateDto dto, User user);

    @Mapping(target = "userName", source = "user.firstName")
    @Mapping(target = "kbk", source = "kbk")
    @Mapping(target = "accountNumber", source = "account.accNumber")
    RequisitesShortDto toShortDto(Requisites requisites);

    default Account map(String accNumber) {
        if (accNumber == null) return null;
        Account account = new Account();
        account.setAccNumber(accNumber);
        return account;
    }

}

