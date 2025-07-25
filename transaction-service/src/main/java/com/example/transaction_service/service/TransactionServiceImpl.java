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

    private boolean accountExists(UUID accountId) {
        try {
            AccountDetailsResponse accountDetails = accountServiceClient.getAccountDetails(accountId);
            System.out.println("ACCOUNT DETAILS: " + accountDetails);
            return accountDetails != null && accountDetails.getAccountId() != null;
        } catch (Exception e) {
            System.out.println("ERROR in accountExists for " + accountId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }

    }

    private boolean hasSufficientFunds(UUID fromAccountId, BigDecimal amount) {
        try {
            AccountDetailsResponse accountDetails = accountServiceClient.getAccountDetails(fromAccountId);
            return accountDetails != null && accountDetails.getBalance() != null
                    && accountDetails.getBalance().compareTo(amount) >= 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public TransferResponse initiateTransaction(TransferRequestInitiation request) {
        if (!accountExists(request.getFromAccountId()) || !accountExists(request.getToAccountId())) {
            throw new AccountNotFoundException("Invalid 'from' or 'to' account ID.");
        }

        if (!hasSufficientFunds(request.getFromAccountId(), request.getAmount())) {
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
            throw new InvalidTransactionStateException("Transaction is not in a valid state for exection.");
        }
        if (!accountExists(transaction.getFromAccountId()) || !accountExists(transaction.getToAccountId())) {
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw new AccountNotFoundException("Invalid 'from' or 'to' account ID.");
        }
        if (!hasSufficientFunds(transaction.getFromAccountId(), transaction.getAmount())) {
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
        if (transactions.isEmpty()) {
            throw new TransactionNotFoundException("No transactions found for account ID: " + accountId + ".");
        }

        return transactions.stream()
                .map(transaction -> new TransactionDetail(
                        transaction.getId(),
                        accountId,
                        transaction.getFromAccountId().equals(accountId) ? transaction.getAmount().negate()
                                : transaction.getAmount(),
                        transaction.getDescription(),
                        transaction.getTimestamp()))
                .toList();

    }

}
