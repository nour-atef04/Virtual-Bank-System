package com.example.user_service.mapper;

import com.example.user_service.dto.LoggableUserLogin;
import com.example.user_service.dto.LoggableUserRegistration;
import com.example.user_service.dto.UserLogin;
import com.example.user_service.dto.UserRegistration;

public class LogMapper {

    public static LoggableUserRegistration toLoggable(UserRegistration request) {

        return new LoggableUserRegistration(
                request.getUsername(),
                request.getEmail(),
                request.getFirstName(),
                request.getLastName());
    }

    public static LoggableUserLogin toLoggable(UserLogin request) {

        return new LoggableUserLogin(
                request.getUsername());
    }

}
