package com.aston.frontendpracticeservice.controller;

import com.aston.frontendpracticeservice.repository.projection.RequisitesProjection;
import com.aston.frontendpracticeservice.service.RequisitesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/requisites")
@RequiredArgsConstructor
public class RequisitesController {

    private final RequisitesService requisitesService;

    @GetMapping
    public List<RequisitesProjection> getAll() {
        return requisitesService.getAllRequisites();
    }

}

