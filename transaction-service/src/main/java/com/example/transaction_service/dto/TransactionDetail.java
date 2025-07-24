package com.example.transaction_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class TransactionDetail {

    private UUID transactionID;
    private UUID accountId;
    private BigDecimal amount;
    private String description;
    private LocalDateTime timestamp;

    public TransactionDetail(UUID transactionID, UUID accountId, BigDecimal amount, String description,
            LocalDateTime timestamp) {
        this.transactionID = transactionID;
        this.accountId = accountId;
        this.amount = amount;
        this.description = description;
        this.timestamp = timestamp;
    }

}

