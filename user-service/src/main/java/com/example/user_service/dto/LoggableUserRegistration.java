package com.example.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoggableUserRegistration {

    private String username;
    private String email;
    private String firstName;
    private String lastName;

}
