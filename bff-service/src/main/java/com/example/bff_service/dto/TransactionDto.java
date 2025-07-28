package com.example.bff_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
        "toAccountId",
        "description",
        "timestamp"
})
public class TransactionDto {
    @JsonProperty("transactionID")
    private String transactionId;
    private double amount;

    @JsonProperty("toAccountId")
    private String toAccountId;
    private String description;

    private String timestamp;
}