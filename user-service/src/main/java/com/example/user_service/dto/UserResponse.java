package com.example.user_service.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {

    private UUID userId;
    private String username;
    private String message;

}
