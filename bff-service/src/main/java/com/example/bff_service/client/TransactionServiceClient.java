package com.example.bff_service.client;

import com.example.bff_service.dto.*;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
                .bodyToMono(new ParameterizedTypeReference<List<TransactionDto>>() {});
    }

    public Mono<TransactionInitiationResponse> initiateTransfer(TransactionInitiationRequest request) {
        return webClient.post()
                .uri("/transactions/transfer/initiation")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(TransactionInitiationResponse.class);
    }

    public Mono<TransactionExecutionResponse> executeTransfer(TransactionExecutionRequest request) {
        return webClient.post()
                .uri("/transactions/transfer/execution")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(TransactionExecutionResponse.class);
    }

}