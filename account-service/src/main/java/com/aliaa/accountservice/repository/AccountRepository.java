package com.aliaa.accountservice.repository;

import com.aliaa.accountservice.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    List<Account> findByUserId(UUID userId);
    boolean existsByAccountNumber(String accountNumber);

    @Query("SELECT a FROM Account a WHERE a.status = 'ACTIVE' AND " +
            "(a.lastActivityAt IS NULL OR a.lastActivityAt < :cutoffTime)")
    List<Account> findInactiveAccounts(@Param("cutoffTime") LocalDateTime cutoffTime);
}