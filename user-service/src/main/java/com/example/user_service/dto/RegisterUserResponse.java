package com.example.user_service.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class RegisterUserResponse {

    private UUID userId;
    private String username;
    private String message;

    public RegisterUserResponse(UUID userId, String username, String message) {
        this.userId = userId;
        this.username = username;
        this.message = message;
    }

}
