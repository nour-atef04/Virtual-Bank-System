package com.example.user_service.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class LoginUserResponse {

        private UUID userId;
    private String username;

    public LoginUserResponse(UUID userId, String username) {
        this.userId = userId;
        this.username = username;
    }

}
