package com.example.user_service.service;

import java.util.UUID;

import com.example.user_service.dto.LoginRequest;
import com.example.user_service.dto.LoginUserResponse;
import com.example.user_service.dto.RegisterRequest;
import com.example.user_service.dto.UserProfileResponse;
import com.example.user_service.dto.RegisterUserResponse;

public interface UserService {

    RegisterUserResponse register(RegisterRequest request);
    LoginUserResponse login(LoginRequest request) throws IllegalAccessException;
    UserProfileResponse getProfileById(UUID userId);

}
