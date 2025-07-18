package com.example.user_service.service;

import java.util.UUID;

import com.example.user_service.dto.UserLogin;
import com.example.user_service.dto.LoginResponse;
import com.example.user_service.dto.UserRegistration;
import com.example.user_service.dto.UserProfile;
import com.example.user_service.dto.UserResponse;

public interface UserService {

    UserResponse register(UserRegistration request);
    LoginResponse login(UserLogin request) throws IllegalAccessException;
    UserProfile getProfileById(UUID userId);

}
