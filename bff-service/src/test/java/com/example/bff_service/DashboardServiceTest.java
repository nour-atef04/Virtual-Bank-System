package com.example.bff_service;

import com.example.bff_service.client.AccountServiceClient;
import com.example.bff_service.client.TransactionServiceClient;
import com.example.bff_service.client.UserServiceClient;
import com.example.bff_service.dto.*;
import com.example.bff_service.service.DashboardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.*;

class DashboardServiceImplTest {

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private AccountServiceClient accountServiceClient;

    @Mock
    private TransactionServiceClient transactionServiceClient;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetDashboardData_Success() {
        String userId = "user123";

        UserProfileDto userProfile = UserProfileDto.builder()
                .userId(userId)
                .username("johndoe")
                .email("john@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        AccountDto account = new AccountDto();
        account.setAccountId("acc1");
        account.setAccountNumber("123456");
        account.setAccountType("SAVINGS");
        account.setBalance(1000.0);

        TransactionDto tx = new TransactionDto();
        tx.setTransactionId("tx1");
        tx.setAmount(200.0);

        when(userServiceClient.getUserProfile(userId)).thenReturn(Mono.just(userProfile));
        when(accountServiceClient.getUserAccounts(userId)).thenReturn(Mono.just(List.of(account)));
        when(transactionServiceClient.getAccountTransactions("acc1")).thenReturn(Mono.just(List.of(tx)));

        StepVerifier.create(dashboardService.getDashboardData(userId))
                .expectNextMatches(response ->
                        response.getUserId().equals("user123") &&
                                response.getAccounts().size() == 1 &&
                                response.getAccounts().get(0).getTransactions().size() == 1)
                .verifyComplete();

        verify(userServiceClient).getUserProfile(userId);
        verify(accountServiceClient).getUserAccounts(userId);
        verify(transactionServiceClient).getAccountTransactions("acc1");
    }

    @Test
    void testGetDashboardData_UserServiceFails() {
        String userId = "user123";

        when(userServiceClient.getUserProfile(userId)).thenReturn(Mono.error(new RuntimeException("User service failed")));

        StepVerifier.create(dashboardService.getDashboardData(userId))
                .expectErrorMatches(e -> e instanceof RuntimeException &&
                        e.getMessage().equals("User service failed"))
                .verify();

        verify(userServiceClient).getUserProfile(userId);
        verifyNoInteractions(accountServiceClient, transactionServiceClient);
    }
}
