package com.example.transaction_service.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.transaction_service.dto.AccountDetailsResponse;
import com.example.transaction_service.dto.TransactionDetail;
import com.example.transaction_service.dto.TransferRequestExecution;
import com.example.transaction_service.dto.TransferRequestInitiation;
import com.example.transaction_service.dto.TransferResponse;
import com.example.transaction_service.model.Transaction;
import com.example.transaction_service.model.TransactionStatus;
import com.example.transaction_service.repository.TransactionRepository;
import com.example.transaction_service.exceptions.*;

import jakarta.transaction.Transactional;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private AccountServiceClient accountServiceClient;

    @Autowired
    private TransactionRepository transactionRepository;

    private AccountDetailsResponse getAccountDetails(UUID accountId) {
        try {
            AccountDetailsResponse details = accountServiceClient.getAccountDetails(accountId);
            if (details == null || details.getAccountId() == null) {
                throw new AccountNotFoundException("Invalid 'from' or 'to' account ID.");
            }
            return details;
        } catch (Exception e) {
            throw new AccountNotFoundException("Invalid 'from' or 'to' account ID.");
        }
    }

    @Override
    public TransferResponse initiateTransaction(TransferRequestInitiation request) {

        AccountDetailsResponse fromAccount = getAccountDetails(request.getFromAccountId());
        AccountDetailsResponse toAccount = getAccountDetails(request.getToAccountId());

        if (fromAccount.getBalance() == null || fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient funds.");
        }

        Transaction transaction = new Transaction();
        transaction.setToAccountId(request.getToAccountId());
        transaction.setFromAccountId(request.getFromAccountId());
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setStatus(TransactionStatus.INITIATED);

        transaction = transactionRepository.save(transaction);

        TransferResponse response = new TransferResponse(
                transaction.getId(),
                transaction.getStatus(),
                transaction.getTimestamp());

        return response;

    }

    @Override
    @Transactional
    public TransferResponse executeTransaction(TransferRequestExecution request) {
        Transaction transaction = transactionRepository.findById(request.getTransactionId())
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found."));

        if (transaction.getStatus() != TransactionStatus.INITIATED) {
            throw new InvalidTransactionStateException("Transaction is not in a valid state for execution.");
        }

        AccountDetailsResponse fromAccount = getAccountDetails(transaction.getFromAccountId());
        AccountDetailsResponse toAccount = getAccountDetails(transaction.getToAccountId());

        if (fromAccount.getBalance() == null || fromAccount.getBalance().compareTo(transaction.getAmount()) < 0) {
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw new InsufficientBalanceException("Insufficient funds.");
        }

        accountServiceClient.transferFunds(
                transaction.getFromAccountId(),
                transaction.getToAccountId(),
                transaction.getAmount());

        transaction.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(transaction);

        TransferResponse response = new TransferResponse(
                transaction.getId(),
                transaction.getStatus(),
                transaction.getTimestamp());
        return response;
    }

    @Override
    public List<TransactionDetail> getTransactionsForAccount(UUID accountId) {
        List<Transaction> transactions = transactionRepository.findByFromAccountIdOrToAccountId(accountId, accountId);

        List<TransactionDetail> successfulTransactions = transactions.stream()
                .filter(transaction -> transaction.getStatus() == TransactionStatus.SUCCESS)
                .map(transaction -> new TransactionDetail(
                        transaction.getId(),
                        accountId,
                        transaction.getFromAccountId().equals(accountId)
                                ? transaction.getAmount().negate()
                                : transaction.getAmount(),
                        transaction.getDescription(),
                        transaction.getTimestamp()))
                .toList();

        return successfulTransactions;
    }

}
