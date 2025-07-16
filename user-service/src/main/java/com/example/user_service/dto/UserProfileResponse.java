package com.example.user_service.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class UserProfileResponse {

    public UserProfileResponse(UUID id, String username2, String email2, String firstName2, String lastName2) {
        //TODO Auto-generated constructor stub
    }
    private UUID userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;

}
