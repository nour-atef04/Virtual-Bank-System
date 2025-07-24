package com.example.transaction_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.transaction_service.dto.TransactionDetail;
import com.example.transaction_service.dto.TransferRequestExecution;
import com.example.transaction_service.dto.TransferRequestInitiation;
import com.example.transaction_service.dto.TransferResponse;
import com.example.transaction_service.service.TransactionService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/transfer/initiation")
    public ResponseEntity<TransferResponse> initiateTransaction(@Valid @RequestBody TransferRequestInitiation request) {

        TransferResponse response = transactionService.initiateTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @PostMapping("/transfer/execution")
    public ResponseEntity<TransferResponse> executeTransaction(@Valid @RequestBody TransferRequestExecution request) {

        TransferResponse response = transactionService.executeTransaction(request);
        return ResponseEntity.ok(response);

    }

    @GetMapping("/account/{accountId}/transactions")
    public ResponseEntity<List<TransactionDetail>> getTransactionsForAccount(@PathVariable UUID accountId) {

        List<TransactionDetail> transactions = transactionService.getTransactionsForAccount(accountId);
        return ResponseEntity.ok(transactions);

    }

}