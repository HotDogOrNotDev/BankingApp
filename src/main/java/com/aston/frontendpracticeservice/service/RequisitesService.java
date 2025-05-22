package com.aston.frontendpracticeservice.service;

import com.aston.frontendpracticeservice.repository.RequisitesRepository;
import com.aston.frontendpracticeservice.repository.projection.RequisitesProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequisitesService {

    private final RequisitesRepository requisitesRepository;

    public List<RequisitesProjection> getAllRequisites() {
        return requisitesRepository.findAllBy();
    }

}