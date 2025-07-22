package com.aliaa.accountservice.service;

import com.aliaa.accountservice.logging.LoggingProducer;
import com.aliaa.accountservice.model.Account;
import com.aliaa.accountservice.model.AccountStatus;
import com.aliaa.accountservice.model.AccountType;
import com.aliaa.accountservice.repository.AccountRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final LoggingProducer loggingProducer;

    @Transactional
    public Account createAccount(UUID userId, AccountType accountType, BigDecimal initialBalance) {

        loggingProducer.sendLog(
                Map.of(
                        "userId", userId,
                        "accountType", accountType,
                        "initialBalance", initialBalance
                ),
                "ACCOUNT_CREATION_REQUEST"
        );

        // Validate input parameters
        validateAccountCreationParameters(accountType, initialBalance);

        // Generate unique account number
        String accountNumber = generateAccountNumber();

        // Create and save account
        Account account = Account.builder()
                .userId(userId)
                .accountNumber(accountNumber)
                .accountType(accountType)
                .balance(initialBalance)
                .status(AccountStatus.ACTIVE)
                .build();

        Account savedAccount = accountRepository.save(account);

        // Log the created account
        loggingProducer.sendLog(savedAccount, "ACCOUNT_CREATED_SUCCESS");
        return savedAccount;
    }

    private void validateAccountCreationParameters(AccountType accountType, BigDecimal initialBalance) {
        if (accountType == null) {
            throw new IllegalArgumentException("Account type is required. Valid types are: SAVINGS, CHECKING");
        }

        if (!AccountType.isValidType(accountType)) {
            throw new IllegalArgumentException("Invalid account type: " + accountType
                    + ". Valid types are: SAVINGS, CHECKING");
        }

        if (initialBalance == null) {
            throw new IllegalArgumentException("Initial balance is required");
        }
        if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
    }

    private String generateAccountNumber() {
        String baseNumber;
        String fullNumber;
        do {
            baseNumber = String.format("%09d", ThreadLocalRandom.current().nextInt(100_000_000, 1_000_000_000));
            fullNumber = baseNumber + calculateChecksum(baseNumber);
        } while (accountRepository.existsByAccountNumber(fullNumber));

        return fullNumber;
    }

    private String calculateChecksum(String baseNumber) {
        int sum = 0;
        for (int i = 0; i < baseNumber.length(); i++) {
            int digit = Character.getNumericValue(baseNumber.charAt(i));
            sum += (i % 2 == 0) ? digit * 2 : digit;
        }
        return String.valueOf((10 - (sum % 10)) % 10);
    }

    public Account getAccountById(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account with ID " + accountId + " not found."));
    }


    @Transactional
    public void transferFunds(UUID fromAccountId, UUID toAccountId, BigDecimal amount) {

        loggingProducer.sendLog(
                Map.of(
                        "fromAccountId", fromAccountId,
                        "toAccountId", toAccountId,
                        "amount", amount
                ),
                "TRANSFER_ATTEMPT"
        );

        // Validate amount
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            loggingProducer.sendLog("Invalid amount: " + amount, "TRANSFER_ERROR");
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        // Fetch accounts (throws IllegalArgumentException if not found)
        Account fromAccount = getAccountById(fromAccountId);
        Account toAccount = getAccountById(toAccountId);

        // Check if accounts are active
        if (fromAccount.getStatus() != AccountStatus.ACTIVE || toAccount.getStatus() != AccountStatus.ACTIVE) {
            loggingProducer.sendLog("Transfer failed: Inactive accounts", "TRANSFER_ERROR");
            throw new IllegalArgumentException("Both accounts must be active");
        }

        // Check sufficient balance
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            loggingProducer.sendLog("Transfer failed: Insufficient balance", "TRANSFER_ERROR");
            throw new IllegalArgumentException("Insufficient balance in source account");
        }

        // Perform transfer
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        // Update last activity timestamp
        LocalDateTime now = LocalDateTime.now();
        fromAccount.setLastActivityAt(now);
        toAccount.setLastActivityAt(now);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        loggingProducer.sendLog(
                Map.of(
                        "fromAccountId", fromAccountId,
                        "toAccountId", toAccountId,
                        "amount", amount,
                        "newFromBalance", fromAccount.getBalance(),
                        "newToBalance", toAccount.getBalance()
                ),
                "TRANSFER_SUCCESS"
        );
    }

    public List<Account> getAccountsByUserId(UUID userId) {
        List<Account> accounts = accountRepository.findByUserId(userId);
        if (accounts.isEmpty()) {
            throw new IllegalArgumentException("No accounts found for user ID " + userId);
        }
        return accounts;
    }



}