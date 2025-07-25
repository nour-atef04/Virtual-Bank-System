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
                .flatMap(user -> accountServiceClient.getUserAccounts(userId)
                        .flatMap(account -> transactionServiceClient.getAccountTransactions(account.getAccountId())
                                .collectList()
                                .map(transactions -> account.withTransactions(transactions)))
                        .collectList()
                        .map(accounts -> buildDashboardResponse(user, accounts))
                        .onErrorMap(e -> e instanceof UserNotFoundException ? e :
                                new ServiceException("Failed to fetch dashboard data: " + e.getMessage())));
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