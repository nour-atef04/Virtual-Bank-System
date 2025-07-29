package com.example.transaction_service.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountDetailsResponse {

    private UUID accountId;
    private BigDecimal balance;

}
