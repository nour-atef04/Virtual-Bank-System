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
import com.example.user_service.dto.LoginRequest;
import com.example.user_service.dto.LoginUserResponse;
import com.example.user_service.dto.RegisterRequest;
import com.example.user_service.dto.UserProfileResponse;
import com.example.user_service.dto.RegisterUserResponse;
import com.example.user_service.service.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(@Valid @RequestBody RegisterRequest request) {

        RegisterUserResponse userResponse = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);

    }

    @PostMapping("/login")
    public ResponseEntity<LoginUserResponse> login(@Valid @RequestBody LoginRequest request) throws IllegalAccessException{

        return ResponseEntity.ok(userService.login(request));

    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfileResponse> getProfile(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getProfileById(userId));
    }

}
