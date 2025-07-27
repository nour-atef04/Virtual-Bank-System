package com.example.bff_service.client;

import com.example.bff_service.dto.UserProfileDto;
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
                .baseUrl("http://" + serviceName)
                .build();
    }

    public Mono<UserProfileDto> getUserProfile(String userId) {
        return webClient.get()
                .uri("/users/{userId}/profile", userId)
                .retrieve()
                .onStatus(
                        status -> status == HttpStatus.NOT_FOUND,
                        response -> Mono.error(new UserNotFoundException("User not found with ID: " + userId))
                )
                .onStatus(status -> status.isError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new ServiceException(
                                        "User service error: " + response.statusCode() + " - " + error))))

                .bodyToMono(UserProfileDto.class);
    }
}