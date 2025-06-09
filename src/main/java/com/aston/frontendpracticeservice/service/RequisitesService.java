package com.aston.frontendpracticeservice.service;

import com.aston.frontendpracticeservice.controller.dto.RequisitesCreateDto;
import com.aston.frontendpracticeservice.controller.dto.RequisitesShortDto;

import java.util.List;
import java.util.UUID;

public interface RequisitesService {

    List<RequisitesShortDto> getAllRequisites();

    RequisitesShortDto create(RequisitesCreateDto dto);

    void deleteRequisite(UUID id);

}
