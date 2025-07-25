package com.example.bff_service.client;

import com.example.bff_service.dto.AccountDto;
import com.example.bff_service.exception.ServiceException;
import lombok.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpStatus;
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
                .onStatus(HttpStatus::isError,
                        response -> response.createException()
                                .flatMap(ex -> Mono.error(new ServiceException(
                                        "Account service error: " + ex.getStatusCode() + " - " + ex.getResponseBodyAsString()))))
                .bodyToFlux(AccountDto.class);
    }
}