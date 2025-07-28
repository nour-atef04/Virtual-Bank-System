package com.example.bff_service.client;

import com.example.bff_service.dto.*;
import com.example.bff_service.exception.ServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

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

    public Mono<List<AccountDto>> getUserAccounts(String userId) {
        return webClient.get()
                .uri("/users/{userId}/accounts", userId)
                .retrieve()
                .onStatus(status -> status.isError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new ServiceException(
                                        "Account service error: " + response.statusCode() + " - " + error))))
                .bodyToMono(new ParameterizedTypeReference<List<AccountDto>>() {});
    }

    public Mono<AccountCreationResponse> createAccount(AccountCreationRequest request) {
        return webClient.post()
                .uri("/accounts")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AccountCreationResponse.class);
    }

    public Mono<AccountTransferResponse> transferFunds(AccountTransferRequest request) {
        return webClient.put()
                .uri("/accounts/transfer")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AccountTransferResponse.class);
    }

    public Mono<AccountDto> getAccountDetails(String accountId) {
        return webClient.get()
                .uri("/accounts/{accountId}", accountId)
                .retrieve()
                .bodyToMono(AccountDto.class);
    }

}