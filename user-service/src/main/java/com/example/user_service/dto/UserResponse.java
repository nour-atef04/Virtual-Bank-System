package com.example.user_service.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class UserResponse {

    public UserResponse(UUID id, String username2, String string) {
        //TODO Auto-generated constructor stub
    }
    private UUID userId;
    private String username;
    private String message;

}
