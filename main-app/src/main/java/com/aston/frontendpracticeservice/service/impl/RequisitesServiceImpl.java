package com.aston.frontendpracticeservice.service.impl;

import com.aston.frontendpracticeservice.controller.dto.RequisitesCreateDto;
import com.aston.frontendpracticeservice.controller.dto.RequisitesShortDto;
import com.aston.frontendpracticeservice.domain.entity.Requisites;
import com.aston.frontendpracticeservice.domain.entity.User;
import com.aston.frontendpracticeservice.exception.UserNotFoundException;
import com.aston.frontendpracticeservice.mapper.RequisitesMapper;
import com.aston.frontendpracticeservice.repository.RequisitesRepository;
import com.aston.frontendpracticeservice.repository.UserRepository;
import com.aston.frontendpracticeservice.service.RequisitesService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequisitesServiceImpl implements RequisitesService {

    private final RequisitesRepository requisitesRepository;
    private final UserRepository userRepository;
    private final RequisitesMapper requisitesMapper;

    public static final String REQUISITES = "requisites";
    public static final String ALL = "'all'";


    @Cacheable(value = REQUISITES, key = ALL)
    @Override
    public List<RequisitesShortDto> getAllRequisites() {
        return requisitesRepository.findAllBy();
    }

    @CacheEvict(value = REQUISITES, key = ALL, beforeInvocation = true)
    @Override
    public RequisitesShortDto create(RequisitesCreateDto dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Requisites entity = requisitesMapper.toEntity(dto, user);
        return requisitesMapper.toShortDto(requisitesRepository.save(entity));
    }

    @CacheEvict(value = REQUISITES, key = ALL, beforeInvocation = true)
    @Override
    public void deleteRequisite(UUID id) {
        requisitesRepository.deleteById(id);
    }

}
