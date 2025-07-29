package com.example.transaction_service.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransferRequestExecution {

    private UUID transactionId;

}
