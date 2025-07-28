package com.example.bff_service.controller;

import com.example.bff_service.client.UserServiceClient;
import com.example.bff_service.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceClient userServiceClient;

    @PostMapping("/register")
    public Mono<ResponseEntity<UserRegistrationResponse>> registerUser(
            @RequestBody UserRegistrationRequest request) {
        return userServiceClient.registerUser(request)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<UserLoginResponse>> loginUser(
            @RequestBody UserLoginRequest request) {
        return userServiceClient.loginUser(request)
                .map(ResponseEntity::ok);
    }
}