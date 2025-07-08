package com.aston.frontendpracticeservice.repository;

import com.aston.frontendpracticeservice.domain.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransferRepository extends JpaRepository<Transfer, UUID> {
}
