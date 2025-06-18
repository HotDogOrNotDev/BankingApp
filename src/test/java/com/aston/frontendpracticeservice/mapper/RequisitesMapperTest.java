package com.aston.frontendpracticeservice.mapper;

import com.aston.frontendpracticeservice.controller.dto.RequisitesCreateDto;
import com.aston.frontendpracticeservice.domain.entity.Requisites;
import com.aston.frontendpracticeservice.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class RequisitesMapperTest {

    private final RequisitesMapper requisitesMapper = Mappers.getMapper(RequisitesMapper.class);

    @Test
    @DisplayName("Mapping from RequisitesCreateDto to Requisites")
    void toEntity_shouldMapAllFields_whenValidDtoAndUserProvided() {
        User user = createDefaultUser();
        RequisitesCreateDto dto = createDefaultRequisitesCreateDto(user.getId());

        Requisites entity = requisitesMapper.toEntity(dto, user);

        assertThat(entity.getAccount().getAccNumber()).isEqualTo(dto.accountNumber());
        assertThat(entity.getBik()).isEqualTo(dto.bik());
        assertThat(entity.getCorrespondentAccount()).isEqualTo(dto.correspondentAccount());
        assertThat(entity.getInn()).isEqualTo(dto.inn());
        assertThat(entity.getKpp()).isEqualTo(dto.kpp());
        assertThat(entity.getKbk()).isEqualTo(dto.kbk());
        assertThat(entity.getUser()).isEqualTo(user);
        assertThat(entity.getUser().getId()).isEqualTo(user.getId());
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

    private RequisitesCreateDto createDefaultRequisitesCreateDto(UUID userId) {
        return RequisitesCreateDto.builder()
                .accountNumber("1234567890")
                .bik("044525225")
                .correspondentAccount("30101810400000000225")
                .inn("1234567890")
                .kpp("123456789")
                .kbk("123456789012345")
                .userId(userId)
                .build();
    }

}