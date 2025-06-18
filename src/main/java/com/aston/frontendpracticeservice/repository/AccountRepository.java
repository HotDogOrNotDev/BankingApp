package com.aston.frontendpracticeservice.repository;

import com.aston.frontendpracticeservice.domain.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
}
