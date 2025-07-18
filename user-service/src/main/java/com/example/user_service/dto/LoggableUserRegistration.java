package com.example.user_service.dto;

import lombok.Data;

@Data
public class LoggableUserRegistration {

    private String username;
    private String email;
    private String firstName;
    private String lastName;

    public LoggableUserRegistration(String username, String email, String firstName, String lastName) {
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

}
