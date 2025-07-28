package com.example.transaction_service.service;

import com.example.transaction_service.dto.*;
import com.example.transaction_service.exceptions.*;
import com.example.transaction_service.model.Transaction;
import com.example.transaction_service.model.TransactionStatus;
import com.example.transaction_service.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountServiceClient accountServiceClient;
    private final TransactionRepository transactionRepository;

    @Override
    public Mono<TransferResponse> initiateTransaction(TransferRequestInitiation request) {
        Mono<AccountDetailsResponse> toAccountMono = accountServiceClient
                .getAccountDetails(request.getToAccountId())
                // .switchIfEmpty(Mono.error(new AccountNotFoundException("To account not
                // found")))
                .cache();

        return accountServiceClient.getAccountDetails(request.getFromAccountId())
                // .switchIfEmpty(Mono.error(new AccountNotFoundException("From account not
                // found")))
                .cache()
                .flatMap(fromAccount -> {
                    if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
                        return Mono.error(new InsufficientBalanceException("Insufficient funds."));
                    }

                    return toAccountMono.flatMap(toAccount -> {
                        return Mono.fromCallable(() -> {
                            Transaction transaction = new Transaction();
                            transaction.setToAccountId(request.getToAccountId());
                            transaction.setFromAccountId(request.getFromAccountId());
                            transaction.setAmount(request.getAmount());
                            transaction.setDescription(request.getDescription());
                            transaction.setStatus(TransactionStatus.INITIATED);
                            transaction = transactionRepository.save(transaction);
                            return new TransferResponse(
                                    transaction.getId(),
                                    transaction.getStatus(),
                                    transaction.getTimestamp());
                        });
                    });
                });

    }

    @Override
    public Mono<TransferResponse> executeTransaction(TransferRequestExecution request) {
        return Mono.fromCallable(() -> transactionRepository.findById(request.getTransactionId())
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found.")))
                .flatMap(foundTransaction -> {
                    if (foundTransaction.getStatus() != TransactionStatus.INITIATED) {
                        return Mono.error(new InvalidTransactionStateException("Invalid transaction state."));
                    }

                    Mono<AccountDetailsResponse> fromAccountMono = accountServiceClient
                            .getAccountDetails(foundTransaction.getFromAccountId())
                            .cache();

                    Mono<AccountDetailsResponse> toAccountMono = accountServiceClient
                            .getAccountDetails(foundTransaction.getToAccountId())
                            .cache();

                    return Mono.zip(fromAccountMono, toAccountMono)
                            .flatMap(tuple -> {
                                AccountDetailsResponse fromAccount = tuple.getT1();
                                AccountDetailsResponse toAccount = tuple.getT2();

                                if (fromAccount.getBalance().compareTo(foundTransaction.getAmount()) < 0) {
                                    foundTransaction.setStatus(TransactionStatus.FAILED);
                                    return Mono.fromCallable(() -> transactionRepository.save(foundTransaction))
                                            .then(Mono.error(new InsufficientBalanceException("Insufficient funds.")));
                                }

                                return accountServiceClient.transferFunds(
                                        foundTransaction.getFromAccountId(),
                                        foundTransaction.getToAccountId(),
                                        foundTransaction.getAmount()).then(Mono.fromCallable(() -> {
                                            foundTransaction.setStatus(TransactionStatus.SUCCESS);
                                            Transaction updated = transactionRepository.save(foundTransaction);
                                            return new TransferResponse(
                                                    updated.getId(),
                                                    updated.getStatus(),
                                                    updated.getTimestamp());
                                        }));
                            });
                });
    }

    @Override
    public Flux<TransactionDetail> getTransactionsForAccount(UUID accountId) {
        return accountServiceClient.getAccountDetails(accountId)
                .then(Mono.fromCallable(() ->
                        transactionRepository.findByFromAccountIdOrToAccountId(accountId, accountId)))
                .flatMapMany(Flux::fromIterable)
                .filter(tx -> tx.getStatus() == TransactionStatus.SUCCESS)
                .map(tx -> {
                    BigDecimal amount = tx.getFromAccountId().equals(accountId)
                            ? tx.getAmount().negate()
                            : tx.getAmount();

                    return new TransactionDetail(
                            tx.getId(),
                            accountId,
                            tx.getToAccountId(),
                            amount,
                            tx.getDescription(),
                            tx.getTimestamp()
                    );
                });
    }


}
