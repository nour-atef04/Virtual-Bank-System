package com.example.bff_service.client;

import com.example.bff_service.dto.TransactionDto;
import com.example.bff_service.exception.ServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class TransactionServiceClient {
    private final WebClient webClient;
    private final String serviceName;

    public TransactionServiceClient(
            @LoadBalanced WebClient.Builder webClientBuilder,
            @Value("${services.transaction-service}") String serviceName
    ) {
        this.serviceName = serviceName;
        this.webClient = webClientBuilder
                .baseUrl("http://" + serviceName)
                .build();
    }

    public Mono<List<TransactionDto>> getAccountTransactions(String accountId) {
        return webClient.get()
                .uri("/accounts/{accountId}/transactions", accountId)
                .retrieve()
                .onStatus(status -> status.isError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new ServiceException(
                                        "Transaction service error: " + response.statusCode() + " - " + error))))
                .bodyToFlux(TransactionDto.class)
                .collectList();
    }

}