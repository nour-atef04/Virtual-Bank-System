package com.example.bff_service.controller;

import com.example.bff_service.client.AccountServiceClient;
import com.example.bff_service.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountServiceClient accountServiceClient;

    @PostMapping
    public Mono<ResponseEntity<AccountCreationResponse>> createAccount(
            @RequestBody AccountCreationRequest request) {
                
        return accountServiceClient.createAccount(request)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/transfer")
    public Mono<ResponseEntity<AccountTransferResponse>> transferFunds(
            @RequestBody AccountTransferRequest request) {
        return accountServiceClient.transferFunds(request)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{accountId}")
    public Mono<ResponseEntity<AccountDto>> getAccountDetails(
            @PathVariable String accountId) {
        return accountServiceClient.getAccountDetails(accountId)
                .map(ResponseEntity::ok);
                // .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}