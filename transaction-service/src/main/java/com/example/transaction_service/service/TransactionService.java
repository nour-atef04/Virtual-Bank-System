package com.example.transaction_service.service;

import java.util.List;
import java.util.UUID;

import com.example.transaction_service.dto.TransactionDetail;
import com.example.transaction_service.dto.TransferRequestExecution;
import com.example.transaction_service.dto.TransferRequestInitiation;
import com.example.transaction_service.dto.TransferResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionService {

    Mono<TransferResponse> initiateTransaction(TransferRequestInitiation request);

    Mono<TransferResponse> executeTransaction(TransferRequestExecution request);

    Flux<TransactionDetail> getTransactionsForAccount(UUID accountId);

}
