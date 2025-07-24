package com.example.transaction_service.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransferRequestInitiation {

    @NotNull(message = "sending account id should not be blank")
    private UUID fromAccountId;

    @NotNull(message = "receiving account id should not be blank")
    private UUID toAccountId;

    @NotNull(message = "amount should not be blank")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    private String description;

}
