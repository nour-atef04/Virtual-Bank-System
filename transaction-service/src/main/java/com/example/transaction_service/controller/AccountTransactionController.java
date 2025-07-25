package com.example.transaction_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.transaction_service.dto.TransactionDetail;
import com.example.transaction_service.service.TransactionService;

@RestController
@RequestMapping("/accounts")
public class AccountTransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<List<TransactionDetail>> getTransactionsForAccount(@PathVariable UUID accountId) {

        System.out.println("Received get account request: " + accountId);

        List<TransactionDetail> transactions = transactionService.getTransactionsForAccount(accountId);

        System.out.println("Returning response: " + transactions);

        return ResponseEntity.ok(transactions);

    }

}
