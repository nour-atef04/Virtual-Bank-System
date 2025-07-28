package com.example.bff_service.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "transactionId",
        "amount",
        "accountId",
        "toAccountId",
        "description",
        "timestamp"
})
public class TransactionDto {
    private String transactionId;
    private double amount;
    private String accountId;
    private String toAccountId;
    private String description;
    private String timestamp;
}