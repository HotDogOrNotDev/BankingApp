package com.aston.frontendpracticeservice.repository;

import com.aston.frontendpracticeservice.controller.dto.RequisitesShortDto;
import com.aston.frontendpracticeservice.domain.entity.Requisites;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface RequisitesRepository extends CrudRepository<Requisites, UUID> {

    @Query("""
           SELECT new com.aston.frontendpracticeservice.controller.dto.RequisitesShortDto(
               r.accountNumber,
               r.kbk,
               r.user.firstName
           )
           FROM Requisites r
           """)
    List<RequisitesShortDto> findAllBy();

}
