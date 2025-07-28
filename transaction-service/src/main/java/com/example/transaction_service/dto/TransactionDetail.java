package com.example.transaction_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class TransactionDetail {

    private UUID transactionId;
    private UUID accountId;
    private UUID toAccountId;
    private BigDecimal amount;
    private String description;
    private LocalDateTime timestamp;

    public TransactionDetail(UUID transactionId, UUID accountId, UUID toAccountId, BigDecimal amount, String description,
            LocalDateTime timestamp) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.description = description;
        this.timestamp = timestamp;
    }

}

