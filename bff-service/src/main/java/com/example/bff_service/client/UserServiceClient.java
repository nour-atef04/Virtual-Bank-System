package com.example.bff_service.client;

import com.example.bff_service.dto.UserProfileDto;
import com.example.bff_service.exception.ServiceException;
import com.example.bff_service.exception.UserNotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
                        HttpStatus.NOT_FOUND::equals,
                        response -> response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> {
                                    String cleanMessage = extractErrorMessage(body, userId);
                                    return Mono.error(new UserNotFoundException(cleanMessage));
                                })
                )
                .onStatus(status -> status.isError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new ServiceException(
                                        "User service error: " + response.statusCode() + " - " + error))))

                .bodyToMono(UserProfileDto.class);

    }


    private String extractErrorMessage(String jsonBody, String userId) {
        try {
            JsonNode jsonNode = new ObjectMapper().readTree(jsonBody);
            return jsonNode.has("message") ? jsonNode.get("message").asText()
                    : "User not found with ID: " + userId;
        } catch (Exception e) {
            return "User not found with ID: " + userId;
        }
    }
}