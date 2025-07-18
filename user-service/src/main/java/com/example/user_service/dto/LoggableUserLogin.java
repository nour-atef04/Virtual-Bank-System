package com.example.user_service.dto;

import lombok.Data;

@Data
public class LoggableUserLogin {

    private String username;

    public LoggableUserLogin(String username) {
        this.username = username;
    }

}
