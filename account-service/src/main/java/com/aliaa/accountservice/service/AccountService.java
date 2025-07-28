package com.aliaa.accountservice.service;

import com.aliaa.accountservice.dto.UserProfileResponse;
import com.aliaa.accountservice.exception.AccountNotFoundException;
import com.aliaa.accountservice.exception.InactiveAccountException;
import com.aliaa.accountservice.exception.InsufficientFundsException;
import com.aliaa.accountservice.exception.InvalidAccountCreationException;
import com.aliaa.accountservice.exception.UserHasNoAccountsException;
import com.aliaa.accountservice.exception.UserProfileNotFoundException;
import com.aliaa.accountservice.model.Account;
import com.aliaa.accountservice.model.AccountStatus;
import com.aliaa.accountservice.model.AccountType;
import com.aliaa.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final WebClient.Builder webClientBuilder;

    @Transactional
    public Mono<Account> createAccount(UUID userId, AccountType accountType, BigDecimal initialBalance) {
        return webClientBuilder.build()
                .get()
                .uri("http://user-service/users/{userId}/profile", userId)
                .retrieve()
                .onStatus(
                        status -> status.is5xxServerError(),
                        response -> Mono.error(new IllegalStateException("User service unavailable"))
                )
                .onStatus(
                        status -> status == HttpStatus.NOT_FOUND,
                        response -> Mono.error(new UserProfileNotFoundException("User with ID " + userId + " not found"))
                )
                .bodyToMono(UserProfileResponse.class)
                .cache()
                .flatMap(userProfile -> {
                    validateAccountCreationParameters(accountType, initialBalance);
                    String accountNumber = generateAccountNumber();

                    Account account = Account.builder()
                            .userId(userId)
                            .accountNumber(accountNumber)
                            .accountType(accountType)
                            .balance(initialBalance)
                            .status(AccountStatus.ACTIVE)
                            .build();

                    return Mono.fromCallable(() -> accountRepository.save(account));
                });
    }

    private void validateAccountCreationParameters(AccountType accountType, BigDecimal initialBalance) {
        if (accountType == null) {
            throw new InvalidAccountCreationException("Account type is required. Valid types are: SAVINGS, CHECKING");
        }

        if (!AccountType.isValidType(accountType)) {
            throw new InvalidAccountCreationException("Invalid account type: " + accountType
                    + ". Valid types are: SAVINGS, CHECKING");
        }

        if (initialBalance == null) {
            throw new InvalidAccountCreationException("Initial balance is required");
        }
        if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAccountCreationException("Initial balance cannot be negative");
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
                .orElseThrow(() -> new AccountNotFoundException("Account with ID " + accountId + " not found"));
    }

    @Transactional
    public void transferFunds(UUID fromAccountId, UUID toAccountId, BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InsufficientFundsException("Amount must be greater than 0");
        }

        Account fromAccount = getAccountById(fromAccountId);
        Account toAccount = getAccountById(toAccountId);

        if (fromAccount.getStatus() != AccountStatus.ACTIVE || toAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new InactiveAccountException("Both accounts must be active");
        }

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient balance in source account");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        LocalDateTime now = LocalDateTime.now();
        fromAccount.setLastActivityAt(now);
        toAccount.setLastActivityAt(now);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
    }

    public List<Account> getAccountsByUserId(UUID userId) {
        List<Account> accounts = accountRepository.findByUserId(userId);
        if (accounts.isEmpty()) {
            throw new UserHasNoAccountsException("No accounts found");
        }
        return accounts;
    }
}