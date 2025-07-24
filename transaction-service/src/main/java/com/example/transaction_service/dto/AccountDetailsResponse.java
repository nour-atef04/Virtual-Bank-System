package com.example.transaction_service.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Data;

@Data
public class AccountDetailsResponse {

    private UUID accountId;
    private BigDecimal balance;

}
