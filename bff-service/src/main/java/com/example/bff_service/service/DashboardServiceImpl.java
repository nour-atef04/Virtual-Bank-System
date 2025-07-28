package com.example.bff_service.service;

import com.example.bff_service.client.AccountServiceClient;
import com.example.bff_service.client.TransactionServiceClient;
import com.example.bff_service.client.UserServiceClient;
import com.example.bff_service.dto.AccountDto;
import com.example.bff_service.dto.DashboardResponse;
import com.example.bff_service.dto.UserProfileDto;
import com.example.bff_service.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final UserServiceClient userServiceClient;
    private final AccountServiceClient accountServiceClient;
    private final TransactionServiceClient transactionServiceClient;

    public Mono<DashboardResponse> getDashboardData(String userId) {
        return userServiceClient.getUserProfile(userId)
                .switchIfEmpty(Mono.defer(() ->
                        Mono.error(new UserNotFoundException("User not found with ID: " + userId))))
                .flatMap(userProfile ->
                        accountServiceClient.getUserAccounts(userId)
                                .defaultIfEmpty(Collections.emptyList())
                                .flatMapMany(Flux::fromIterable)
                                .flatMap(account ->
                                        transactionServiceClient.getAccountTransactions(account.getAccountId())
                                                .defaultIfEmpty(Collections.emptyList())
                                                .onErrorResume(e -> Mono.just(Collections.emptyList()))
                                                .map(transactions -> {
                                                    AccountDto accountWithTransactions = new AccountDto();
                                                    accountWithTransactions.setAccountId(account.getAccountId());
                                                    accountWithTransactions.setAccountNumber(account.getAccountNumber());
                                                    accountWithTransactions.setAccountType(account.getAccountType());
                                                    accountWithTransactions.setBalance(account.getBalance());
                                                    accountWithTransactions.setTransactions(transactions);
                                                    return accountWithTransactions;
                                                })
                                )
                                .collectList()
                                .map(accounts -> buildDashboardResponse(userProfile, accounts))
                )
                .cache()
                .onErrorResume(UserNotFoundException.class, e ->
                        Mono.error(e) // Re-throw to let the controller handle it
                );
    }

    private DashboardResponse buildDashboardResponse(UserProfileDto userProfile, List<AccountDto> accounts) {
        return DashboardResponse.builder()
                .userId(userProfile.getUserId())
                .username(userProfile.getUsername())
                .email(userProfile.getEmail())
                .firstName(userProfile.getFirstName())
                .lastName(userProfile.getLastName())
                .accounts(accounts)
                .build();
    }
}