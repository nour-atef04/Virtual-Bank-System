package com.example.transaction_service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.transaction_service.model.TransactionStatus;

import lombok.Data;

@Data
public class TransferResponse {

    private UUID transactionId;
    private TransactionStatus status;
    private LocalDateTime timestamp;

    public TransferResponse(UUID transactionId, TransactionStatus status, LocalDateTime timestamp) {
        this.transactionId = transactionId;
        this.status = status;
        this.timestamp = timestamp;
    }

}

