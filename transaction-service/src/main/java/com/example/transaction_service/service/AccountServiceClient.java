package com.example.transaction_service.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.transaction_service.dto.AccountDetailsResponse;
import com.example.transaction_service.dto.TransferRequest;

@Service
public class AccountServiceClient {

    private WebClient webClient;

    public AccountServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://account-service/accounts").build();
    }

    public AccountDetailsResponse getAccountDetails(UUID accountId) {
        return webClient.get()
                .uri("/{accountId}", accountId)
                .retrieve()
                .bodyToMono(AccountDetailsResponse.class)
                .block();
    }

    public void transferFunds(UUID fromAccountId, UUID toAccountId, BigDecimal amount) {

        TransferRequest transferRequest = new TransferRequest(fromAccountId, toAccountId, amount);

        webClient.post()
                .uri("/transfer")
                .bodyValue(transferRequest)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

}
