package com.example.bff_service.client;

import com.example.bff_service.dto.*;
import com.example.bff_service.exception.ServiceException;
import com.example.bff_service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UserServiceClient {
    private final WebClient webClient;

    public UserServiceClient(
            @LoadBalanced WebClient.Builder webClientBuilder,
            @Value("${services.user-service}") String serviceName
    ) {
        this.webClient = webClientBuilder
                .baseUrl("http://" + serviceName + "/users")
                .build();
    }

    public Mono<UserProfileDto> getUserProfile(String userId) {
        return webClient.get()
                .uri("{userId}/profile", userId)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response ->
                        Mono.error(new UserNotFoundException(userId)))
                .bodyToMono(UserProfileDto.class);
    }

    public Mono<UserRegistrationResponse> registerUser(UserRegistrationRequest request) {
        return webClient.post()
                .uri("/register")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatus.CONFLICT::equals,
                        response -> Mono.error(new ServiceException("Username or email already exists")))
                .bodyToMono(UserRegistrationResponse.class);
    }

    public Mono<UserLoginResponse> loginUser(UserLoginRequest request) {
        return webClient.post()
                .uri("/login")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatus.UNAUTHORIZED::equals,
                        response -> Mono.error(new ServiceException("Invalid credentials")))
                .bodyToMono(UserLoginResponse.class);
    }


}