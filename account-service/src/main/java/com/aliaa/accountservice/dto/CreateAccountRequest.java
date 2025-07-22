package com.aliaa.accountservice.dto;

import com.aliaa.accountservice.model.AccountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest {

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "Account type is required")
    private AccountType accountType;

    @NotNull(message = "Initial balance is required")
    @DecimalMin(value = "0.00", message = "Initial balance must be at least 0")
    private BigDecimal initialBalance;
}