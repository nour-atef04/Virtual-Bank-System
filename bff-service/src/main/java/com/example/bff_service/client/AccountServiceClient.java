package com.example.bff_service.client;

import com.example.bff_service.dto.AccountDto;
import com.example.bff_service.exception.ServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class AccountServiceClient {
    private final WebClient webClient;
    private final String serviceName;

    public AccountServiceClient(
            @LoadBalanced WebClient.Builder webClientBuilder,
            @Value("${services.account-service}") String serviceName
    ) {
        this.serviceName = serviceName;
        this.webClient = webClientBuilder
                .baseUrl("http://" + serviceName)
                .build();
    }

    public Flux<AccountDto> getUserAccounts(String userId) {
        return webClient.get()
                .uri("/users/{userId}/accounts", userId)
                .retrieve()
                .onStatus(status -> status.isError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new ServiceException(
                                        "Account service error: " + response.statusCode() + " - " + error))))
                .bodyToFlux(AccountDto.class);
    }
}