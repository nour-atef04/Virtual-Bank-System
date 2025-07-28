package com.example.bff_service.client;

import com.example.bff_service.dto.TransactionDto;
import com.example.bff_service.exception.ServiceException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Component
public class TransactionServiceClient {
    private final WebClient webClient;
    private final String serviceName;
    private final ObjectMapper objectMapper;

    public TransactionServiceClient(
            @LoadBalanced WebClient.Builder webClientBuilder,
            @Value("${services.transaction-service}") String serviceName,
            ObjectMapper objectMapper) {
        this.serviceName = serviceName;
        this.webClient = webClientBuilder.baseUrl("http://" + serviceName).build();
        this.objectMapper = objectMapper;
        this.objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
    }

    public Mono<List<TransactionDto>> getAccountTransactions(String accountId) {
        return webClient.get()
                .uri("/accounts/{accountId}/transactions", accountId)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> System.out.println("Raw response: " + response)) // Debug logging
                .flatMap(response -> {
                    try {
                        // Parse the response
                        List<TransactionDto> transactions = objectMapper.readValue(
                                response,
                                new TypeReference<List<TransactionDto>>() {}
                        );

                        // Log the parsed transactions for debugging
                        System.out.println("Parsed transactions: " + transactions);

                        return Mono.just(transactions);
                    } catch (Exception e) {
                        System.err.println("Error parsing transactions: " + e.getMessage());
                        return Mono.error(new ServiceException("Failed to parse transactions"));
                    }
                })
                .onErrorResume(e -> {
                    System.err.println("Error fetching transactions: " + e.getMessage());
                    return Mono.just(Collections.emptyList());
                });
    }
}