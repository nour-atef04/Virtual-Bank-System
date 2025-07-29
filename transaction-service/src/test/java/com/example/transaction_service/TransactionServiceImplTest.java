package com.example.transaction_service;

import com.example.transaction_service.dto.*;
import com.example.transaction_service.exceptions.*;
import com.example.transaction_service.model.*;
import com.example.transaction_service.repository.TransactionRepository;
import com.example.transaction_service.service.AccountServiceClient;
import com.example.transaction_service.service.TransactionServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private AccountServiceClient accountServiceClient;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    void initiateTransaction_shouldReturnTransferResponse_whenValid() {
        UUID fromId = UUID.randomUUID();
        UUID toId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(100);

        TransferRequestInitiation request = new TransferRequestInitiation(fromId, toId, amount, "test transfer");

        AccountDetailsResponse fromAccount = new AccountDetailsResponse(fromId, BigDecimal.valueOf(150));
        AccountDetailsResponse toAccount = new AccountDetailsResponse(toId, BigDecimal.valueOf(50));

        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setFromAccountId(fromId);
        transaction.setToAccountId(toId);
        transaction.setAmount(amount);
        transaction.setStatus(TransactionStatus.INITIATED);
        transaction.setTimestamp(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));

        when(accountServiceClient.getAccountDetails(fromId)).thenReturn(Mono.just(fromAccount));
        when(accountServiceClient.getAccountDetails(toId)).thenReturn(Mono.just(toAccount));
        when(transactionRepository.save(any())).thenReturn(transaction);

        Mono<TransferResponse> result = transactionService.initiateTransaction(request);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(transaction.getId(), response.getTransactionId());
                    assertEquals(TransactionStatus.INITIATED, response.getStatus());
                })
                .verifyComplete();
    }

    @Test
    void initiateTransaction_shouldFail_whenInsufficientBalance() {
        UUID fromId = UUID.randomUUID();
        UUID toId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(100);

        TransferRequestInitiation request = new TransferRequestInitiation(fromId, toId, amount, "test transfer");

        AccountDetailsResponse fromAccount = new AccountDetailsResponse(fromId, BigDecimal.valueOf(50));

        when(accountServiceClient.getAccountDetails(any(UUID.class))).thenReturn(Mono.just(fromAccount));

        Mono<TransferResponse> result = transactionService.initiateTransaction(request);

        StepVerifier.create(result)
                .expectError(InsufficientBalanceException.class)
                .verify();
    }

    @Test
    void executeTransaction_shouldCompleteSuccessfully_whenValid() {
        UUID txId = UUID.randomUUID();
        UUID fromId = UUID.randomUUID();
        UUID toId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(100);

        Transaction transaction = new Transaction();
        transaction.setId(txId);
        transaction.setFromAccountId(fromId);
        transaction.setToAccountId(toId);
        transaction.setAmount(amount);
        transaction.setStatus(TransactionStatus.INITIATED);
        transaction.setTimestamp(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));

        Transaction updatedTx = new Transaction();
        updatedTx.setId(txId);
        updatedTx.setStatus(TransactionStatus.SUCCESS);
        updatedTx.setTimestamp(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));

        when(transactionRepository.findById(txId)).thenReturn(Optional.of(transaction));
        when(accountServiceClient.transferFunds(fromId, toId, amount)).thenReturn(Mono.empty());
        when(transactionRepository.save(any())).thenReturn(updatedTx);

        Mono<TransferResponse> result = transactionService.executeTransaction(new TransferRequestExecution(txId));

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(txId, response.getTransactionId());
                    assertEquals(TransactionStatus.SUCCESS, response.getStatus());
                })
                .verifyComplete();
    }

    @Test
    void executeTransaction_shouldFail_whenTransactionNotFound() {
        UUID txId = UUID.randomUUID();
        when(transactionRepository.findById(txId)).thenReturn(Optional.empty());

        Mono<TransferResponse> result = transactionService.executeTransaction(new TransferRequestExecution(txId));

        StepVerifier.create(result)
                .expectError(TransactionNotFoundException.class)
                .verify();
    }

    @Test
    void executeTransaction_shouldFail_whenTransactionInvalidState() {
        UUID transactionId = UUID.randomUUID();
        Transaction transaction = new Transaction();
        transaction.setId(transactionId);
        transaction.setStatus(TransactionStatus.SUCCESS); 

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        Mono<TransferResponse> result = transactionService.executeTransaction(new TransferRequestExecution(transactionId));

        StepVerifier.create(result)
                .expectError(InvalidTransactionStateException.class)
                .verify();
    }

    @Test
    void executeTransaction_shouldSetFailedStatus_whenTransferFails() {
        UUID transactionId = UUID.randomUUID();
        UUID fromId = UUID.randomUUID();
        UUID toId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(100);

        Transaction transaction = new Transaction();
        transaction.setId(transactionId);
        transaction.setFromAccountId(fromId);
        transaction.setToAccountId(toId);
        transaction.setAmount(amount);
        transaction.setStatus(TransactionStatus.INITIATED);
        transaction.setTimestamp(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(accountServiceClient.transferFunds(fromId, toId, amount))
                .thenReturn(Mono.error(new RuntimeException("Transfer failed")));
        when(transactionRepository.save(any())).thenReturn(transaction); 

        Mono<TransferResponse> result = transactionService.executeTransaction(new TransferRequestExecution(transactionId));

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        assertEquals(TransactionStatus.FAILED, transaction.getStatus());
    }
}
