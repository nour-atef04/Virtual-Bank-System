package com.aliaa.accountservice.service;

import com.aliaa.accountservice.config.AccountInactivityConfig;
import com.aliaa.accountservice.logging.LoggingProducer;
import com.aliaa.accountservice.model.Account;
import com.aliaa.accountservice.model.AccountStatus;
import com.aliaa.accountservice.repository.AccountRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountMaintenanceService {
    private final AccountRepository accountRepository;
    private final AccountInactivityConfig config;
    private final LoggingProducer loggingProducer;

    @Scheduled(fixedRateString = "${account.inactivity.check-interval-minutes}",
            timeUnit = TimeUnit.MINUTES)

    @Transactional
    public void markInactiveAccounts() {
        LocalDateTime cutoffTime = LocalDateTime.now()
                .minusMinutes(config.getThresholdMinutes());

        List<Account> inactiveAccounts = accountRepository.findInactiveAccounts(cutoffTime);

        // Safe logging of detection event
        loggingProducer.sendLog(
                Map.of(
                        "cutoffTime", cutoffTime.toString(), // Ensure non-null
                        "inactiveAccountsCount", inactiveAccounts.size()
                ),
                "INACTIVE_ACCOUNTS_DETECTED"
        );

        inactiveAccounts.forEach(account -> {
            if (account != null) {  // Null check for account
                account.setStatus(AccountStatus.INACTIVE);

                // Safe logging with null checks
                Map<String, Object> logData = new HashMap<>();
                logData.put("accountId", account.getId() != null ? account.getId().toString() : "null");
                logData.put("lastActivity", account.getLastActivityAt() != null ?
                        account.getLastActivityAt().toString() : "never");
                logData.put("status", "INACTIVE");

                loggingProducer.sendLog(logData, "ACCOUNT_MARKED_INACTIVE");
            }
        });

        accountRepository.saveAll(inactiveAccounts);
    }

}