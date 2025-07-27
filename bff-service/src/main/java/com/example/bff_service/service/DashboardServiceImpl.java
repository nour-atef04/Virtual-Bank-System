package com.example.bff_service.service;

import com.example.bff_service.client.AccountServiceClient;
import com.example.bff_service.client.TransactionServiceClient;
import com.example.bff_service.client.UserServiceClient;
import com.example.bff_service.dto.AccountDto;
import com.example.bff_service.dto.DashboardResponse;
import com.example.bff_service.dto.UserProfileDto;
import com.example.bff_service.exception.ServiceException;
import com.example.bff_service.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final UserServiceClient userServiceClient;
    private final AccountServiceClient accountServiceClient;
    private final TransactionServiceClient transactionServiceClient;

    public Mono<DashboardResponse> getDashboardData(String userId) {
        return userServiceClient.getUserProfile(userId)
                .flatMap(userProfile ->
                        accountServiceClient.getUserAccounts(userId) // Mono<List<AccountDto>>
                                .flatMapMany(Flux::fromIterable) // Convert to Flux<AccountDto>
                                .flatMap(account ->
                                        transactionServiceClient.getAccountTransactions(account.getAccountId())
                                                .map(account::withTransactions)
                                )
                                .collectList() // back to Mono<List<AccountDto>>
                                .map(accounts -> buildDashboardResponse(userProfile, accounts))
                )
                .onErrorMap(ex -> {
                    if (ex instanceof UserNotFoundException) {
                        return ex;
                    }
                    return new ServiceException("Failed to fetch dashboard data: " + ex.getMessage());
                });
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