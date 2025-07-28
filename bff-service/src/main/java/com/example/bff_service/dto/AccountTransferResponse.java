package com.example.bff_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountTransferResponse {
    private String message;
    public static AccountTransferResponse error(String message) {
        return AccountTransferResponse.builder()
                .message(message)
                .build();
    }
}