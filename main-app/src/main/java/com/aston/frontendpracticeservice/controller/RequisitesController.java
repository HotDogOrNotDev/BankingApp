package com.aston.frontendpracticeservice.controller;

import com.aston.frontendpracticeservice.controller.dto.RequisitesCreateDto;
import com.aston.frontendpracticeservice.controller.dto.RequisitesShortDto;
import com.aston.frontendpracticeservice.service.RequisitesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/requisites")
@RequiredArgsConstructor
public class RequisitesController {

    private final RequisitesService requisitesService;

    @GetMapping
    public List<RequisitesShortDto> getAll() {
        return requisitesService.getAllRequisites();
    }

    @PostMapping
    public RequisitesShortDto createRequisite(@RequestBody RequisitesCreateDto dto) {
        return requisitesService.create(dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        requisitesService.deleteRequisite(id);
    }

}

