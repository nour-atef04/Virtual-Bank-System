package com.example.user_service.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class LoginResponse {

    private UUID userId;
    private String username;

    public LoginResponse(UUID userId, String username) {
        this.userId = userId;
        this.username = username;
    }

}
