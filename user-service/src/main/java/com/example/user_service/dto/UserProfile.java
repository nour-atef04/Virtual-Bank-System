package com.example.user_service.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class UserProfile {

    public UserProfile(UUID userId, String username, String email, String firstName, String lastName) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    private UUID userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;

}
