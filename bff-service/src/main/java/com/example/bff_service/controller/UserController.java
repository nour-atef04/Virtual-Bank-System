package com.example.bff_service.controller;

import com.example.bff_service.client.UserServiceClient;
import com.example.bff_service.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceClient userServiceClient;

    @GetMapping("/{userId}/profile")
    public Mono<ResponseEntity<UserProfileDto>> getProfile(@PathVariable String userId) {
        return userServiceClient.getUserProfile(userId)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<AppNameWrappedResponse<UserRegistrationResponse>>> registerUser(
            @RequestBody UserRegistrationRequest request, @RequestHeader("APP-NAME") String appName) {
        return userServiceClient.registerUser(request)
                .map(response -> {
                    AppNameWrappedResponse<UserRegistrationResponse> wrapped = new AppNameWrappedResponse<>(appName,
                            response);
                    return ResponseEntity.ok(wrapped);
                });
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<AppNameWrappedResponse<UserLoginResponse>>> loginUser(
            @RequestBody UserLoginRequest request, @RequestHeader("APP-NAME") String appName) {
        return userServiceClient.loginUser(request)
                .map(response -> {
                    AppNameWrappedResponse<UserLoginResponse> wrapped = new AppNameWrappedResponse<>(appName,
                            response);
                    return ResponseEntity.ok(wrapped);
                });
    }
}