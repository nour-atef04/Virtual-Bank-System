package com.example.transaction_service.service;

import java.util.List;
import java.util.UUID;

import com.example.transaction_service.dto.TransactionDetail;
import com.example.transaction_service.dto.TransferRequestExecution;
import com.example.transaction_service.dto.TransferRequestInitiation;
import com.example.transaction_service.dto.TransferResponse;

public interface TransactionService {

    TransferResponse initiateTransaction(TransferRequestInitiation request);

    TransferResponse executeTransaction(TransferRequestExecution request);

    List<TransactionDetail> getTransactionsForAccount(UUID accountId);

}
