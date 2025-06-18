package com.aston.frontendpracticeservice.repository;

import com.aston.frontendpracticeservice.controller.dto.RequisitesShortDto;
import com.aston.frontendpracticeservice.domain.entity.Requisites;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface RequisitesRepository extends JpaRepository<Requisites, UUID> {

    @Query("""
    SELECT new com.aston.frontendpracticeservice.controller.dto.RequisitesShortDto(
        requisites.account.accNumber,
        requisites.kbk,
        requisites.user.firstName
    )
    FROM Requisites requisites
    """)
    List<RequisitesShortDto> findAllBy();

}
