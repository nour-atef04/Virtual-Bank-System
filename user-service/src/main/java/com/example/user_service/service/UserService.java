package com.example.user_service.service;

import java.util.UUID;

import com.example.user_service.dto.LoginRequest;
import com.example.user_service.dto.RegisterRequest;
import com.example.user_service.dto.UserProfileResponse;
import com.example.user_service.dto.UserResponse;

public interface UserService {

    UserResponse register(RegisterRequest request);
    UserResponse login(LoginRequest request);
    UserProfileResponse getProfileById(UUID userId);

}
