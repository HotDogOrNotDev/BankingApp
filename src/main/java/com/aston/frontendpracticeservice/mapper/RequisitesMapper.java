package com.aston.frontendpracticeservice.mapper;

import com.aston.frontendpracticeservice.controller.dto.RequisitesCreateDto;
import com.aston.frontendpracticeservice.controller.dto.RequisitesShortDto;
import com.aston.frontendpracticeservice.domain.entity.Requisites;
import com.aston.frontendpracticeservice.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.Optional;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RequisitesMapper {

    @Mapping(target = "accountNumber", source = "dto.accountNumber")
    @Mapping(target = "bik", source = "dto.bik")
    @Mapping(target = "correspondentAccount", source = "dto.correspondentAccount")
    @Mapping(target = "inn", source = "dto.inn")
    @Mapping(target = "kpp", source = "dto.kpp")
    @Mapping(target = "kbk", source = "dto.kbk")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "id", ignore = true)
    Requisites toEntity(RequisitesCreateDto dto, User user);

    @Mapping(target = "userName", source = "requisites", qualifiedByName = "extractUserName")
    RequisitesShortDto toShortDto(Requisites requisites);

    @Named("extractUserName")
    default String extractUserName(Requisites requisites) {
        return Optional.ofNullable(requisites)
                .map(Requisites::getUser)
                .map(User::getFirstName)
                .orElse(null);
    }

}
