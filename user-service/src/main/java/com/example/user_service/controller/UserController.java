package com.example.user_service.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.user_service.dto.UserLogin;
import com.example.user_service.dto.LoginResponse;
import com.example.user_service.dto.UserRegistration;
import com.example.user_service.dto.UserProfile;
import com.example.user_service.dto.UserResponse;
import com.example.user_service.logging.LoggingProducer;
import com.example.user_service.mapper.LogMapper;
import com.example.user_service.service.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private LoggingProducer loggingProducer;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegistration request) {
        loggingProducer.sendLog(LogMapper.toLoggable(request), "Request");
        UserResponse userResponse = userService.register(request);
        loggingProducer.sendLog(userResponse, "Response");
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody UserLogin request) throws IllegalAccessException {
        loggingProducer.sendLog(LogMapper.toLoggable(request), "Request");
        LoginResponse loginResponse = userService.login(request);
        loggingProducer.sendLog(loginResponse, "Response");
        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfile> getProfile(@PathVariable UUID userId) {
        loggingProducer.sendLog(java.util.Collections.singletonMap("userId", userId), "Request");
        UserProfile userProfile = userService.getProfileById(userId);
        loggingProducer.sendLog(userProfile, "Response");
        return ResponseEntity.ok(userProfile);
    }

}
