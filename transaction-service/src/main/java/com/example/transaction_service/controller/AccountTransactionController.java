package com.example.transaction_service.controller;

import com.example.transaction_service.dto.TransactionDetail;
import com.example.transaction_service.service.TransactionService;

import reactor.core.publisher.Mono;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountTransactionController {

    private final TransactionService transactionService;

    public AccountTransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{accountId}/transactions")
    public Mono<ResponseEntity<List<TransactionDetail>>> getTransactionsForAccount(@PathVariable UUID accountId) {
        return transactionService.getTransactionsForAccount(accountId)
                .collectList()
                .map(list -> ResponseEntity.ok(list));
    }
}
