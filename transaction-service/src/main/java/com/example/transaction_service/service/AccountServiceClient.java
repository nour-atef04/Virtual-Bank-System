package com.example.transaction_service.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.transaction_service.dto.AccountDetailsResponse;
import com.example.transaction_service.dto.TransferRequest;
import com.example.transaction_service.exceptions.AccountNotFoundException;
import com.example.transaction_service.exceptions.InsufficientBalanceException;

import reactor.core.publisher.Mono;

@Service
public class AccountServiceClient {

    private WebClient webClient;

    public AccountServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://account-service/accounts").build();
    }

    public Mono<AccountDetailsResponse> getAccountDetails(UUID accountId) {
        return webClient.get()
                .uri("/{accountId}", accountId)
                .retrieve()
                // .onStatus(
                // status -> status.is5xxServerError(),
                // response -> Mono.error(new IllegalStateException("Account service
                // unavailable")))
                // .onStatus(
                // status -> status == HttpStatus.NOT_FOUND,
                // response -> Mono.error(new AccountNotFoundException("Invalid 'from' or 'to'
                // account ID.")))
                .onStatus(HttpStatusCode::isError, WebClientErrorHandler::handle)
                .bodyToMono(AccountDetailsResponse.class);
    }

    public Mono<Void> transferFunds(UUID fromAccountId, UUID toAccountId, BigDecimal amount) {
        TransferRequest transferRequest = new TransferRequest(fromAccountId, toAccountId, amount);

        return webClient.put()
                .uri("/transfer")
                .bodyValue(transferRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, WebClientErrorHandler::handle)
                .bodyToMono(Void.class);
    }

}
