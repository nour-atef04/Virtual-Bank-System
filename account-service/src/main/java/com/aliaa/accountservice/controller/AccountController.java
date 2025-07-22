package com.aliaa.accountservice.controller;

import com.aliaa.accountservice.dto.AccountDetailsResponse;
import com.aliaa.accountservice.dto.AccountResponse;
import com.aliaa.accountservice.dto.CreateAccountRequest;
import com.aliaa.accountservice.dto.TransferRequest;
import com.aliaa.accountservice.model.Account;
import com.aliaa.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<?> createAccount(
            @Valid @RequestBody CreateAccountRequest request) {
            Account account = accountService.createAccount(
                    request.getUserId(),
                    request.getAccountType(),
                    request.getInitialBalance());

            AccountResponse response = AccountResponse.builder()
                    .accountId(account.getId())
                    .accountNumber(account.getAccountNumber())
                    .message("Account created successfully.")
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        }


    @GetMapping("/{accountId}")
    public ResponseEntity<?> getAccountDetails(@PathVariable UUID accountId) {
        Account account = accountService.getAccountById(accountId);

        AccountDetailsResponse response = AccountDetailsResponse.builder()
                .accountId(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .status(account.getStatus())
                .build();

        return ResponseEntity.ok(response);
    }


    @PutMapping("/transfer")
    public ResponseEntity<?> transferFunds(
            @Valid @RequestBody TransferRequest request
    ) {
        accountService.transferFunds(
                request.getFromAccountId(),
                request.getToAccountId(),
                request.getAmount()
        );

        return ResponseEntity.ok().body(
                Map.of("message", "Transfer successful.")
        );
    }



}