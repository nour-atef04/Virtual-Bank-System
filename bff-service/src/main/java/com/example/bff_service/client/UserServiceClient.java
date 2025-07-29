package com.example.bff_service.client;

import com.example.bff_service.dto.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpStatusCode;

@Component
public class UserServiceClient {
        private final WebClient webClient;

        public UserServiceClient(
                        @LoadBalanced WebClient.Builder webClientBuilder,
                        @Value("${services.user-service}") String serviceName) {
                this.webClient = webClientBuilder
                                .baseUrl("http://" + serviceName + "/users")
                                .build();
        }

        public Mono<UserProfileDto> getUserProfile(String userId) {
                return webClient.get()
                                .uri("/{userId}/profile", userId)
                                .retrieve()
                                .onStatus(HttpStatusCode::isError, WebClientErrorHandler::handle)
                                .bodyToMono(UserProfileDto.class);
        }

        public Mono<UserRegistrationResponse> registerUser(UserRegistrationRequest request) {
                return webClient.post()
                                .uri("/register")
                                .bodyValue(request)
                                .retrieve()
                                .onStatus(HttpStatusCode::isError, WebClientErrorHandler::handle)
                                .bodyToMono(UserRegistrationResponse.class);
        }

        public Mono<UserLoginResponse> loginUser(UserLoginRequest request) {
                return webClient.post()
                                .uri("/login")
                                .bodyValue(request)
                                .retrieve()
                                .onStatus(HttpStatusCode::isError, WebClientErrorHandler::handle)
                                .bodyToMono(UserLoginResponse.class);
        }

}