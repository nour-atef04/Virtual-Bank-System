package com.example.user_service.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class UserResponse {

    private UUID userId;
    private String username;
    private String message;

    public UserResponse(UUID userId, String username, String message) {
        this.userId = userId;
        this.username = username;
        this.message = message;
    }

}
