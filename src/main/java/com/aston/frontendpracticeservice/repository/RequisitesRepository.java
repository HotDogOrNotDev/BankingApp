package com.aston.frontendpracticeservice.repository;

import com.aston.frontendpracticeservice.domain.entity.Requisites;
import com.aston.frontendpracticeservice.repository.projection.RequisitesProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RequisitesRepository extends JpaRepository<Requisites, UUID> {

    List<RequisitesProjection> findAllBy();

}
