package com.aliaa.accountservice.controller;

import com.aliaa.accountservice.dto.AccountDetailsResponse;
import com.aliaa.accountservice.model.Account;
import com.aliaa.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final AccountService accountService;

    @GetMapping("/{userId}/accounts")
    public ResponseEntity<?> getUserAccounts(@PathVariable UUID userId) {
        List<Account> accounts = accountService.getAccountsByUserId(userId);

        List<AccountDetailsResponse> response = accounts.stream()
                .map(account -> AccountDetailsResponse.builder()
                        .accountId(account.getId())
                        .accountNumber(account.getAccountNumber())
                        .accountType(account.getAccountType())
                        .balance(account.getBalance())
                        .status(account.getStatus())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}