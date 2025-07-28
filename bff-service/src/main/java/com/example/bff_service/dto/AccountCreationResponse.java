package com.example.bff_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreationResponse {
    private String accountId;
    private String accountNumber;
    private String message;
    private boolean success;

    public static AccountCreationResponse error(String message) {
        return AccountCreationResponse.builder()
                .message(message)
                .success(false)
                .build();
    }
}