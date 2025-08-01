package com.aliaa.accountservice;

import com.aliaa.accountservice.dto.UserProfileResponse;
import com.aliaa.accountservice.exception.AccountNotFoundException;
import com.aliaa.accountservice.exception.InsufficientFundsException;
import com.aliaa.accountservice.model.Account;
import com.aliaa.accountservice.model.AccountStatus;
import com.aliaa.accountservice.model.AccountType;
import com.aliaa.accountservice.repository.AccountRepository;
import com.aliaa.accountservice.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;
    
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private AccountService accountService;

    private UUID testUserId;
    private Account testAccount;
    private UserProfileResponse testUserProfile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUserId = UUID.randomUUID();
        testAccount = Account.builder()
                .id(UUID.randomUUID())
                .userId(testUserId)
                .accountNumber("1234567890")
                .accountType(AccountType.SAVINGS)
                .balance(BigDecimal.valueOf(1000))
                .status(AccountStatus.ACTIVE)
                .lastActivityAt(LocalDateTime.now())
                .build();

        testUserProfile = new UserProfileResponse(
                testUserId,
                "testuser",
                "test@example.com",
                "Test",
                "User"
        );
    }

    @Test
    void createAccount_Success() {
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(UUID.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserProfileResponse.class)).thenReturn(Mono.just(testUserProfile));

        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        Account createdAccount = accountService.createAccount(
                testUserId,
                "SAVINGS",
                BigDecimal.valueOf(1000)
        ).block();

        assertNotNull(createdAccount);
        assertEquals(testUserId, createdAccount.getUserId());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void getAccountById_Success() {
        UUID accountId = testAccount.getId();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(testAccount));

        Account foundAccount = accountService.getAccountById(accountId);

        assertNotNull(foundAccount);
        assertEquals(testAccount.getId(), foundAccount.getId());
        verify(accountRepository).findById(accountId);
    }

    @Test
    void getAccountById_NotFound() {
        when(accountRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () ->
                accountService.getAccountById(UUID.randomUUID()));
    }

    @Test
    void transferFunds_Success() {
        Account targetAccount = Account.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .accountNumber("0987654321")
                .accountType(AccountType.CHECKING)
                .balance(BigDecimal.valueOf(500))
                .status(AccountStatus.ACTIVE)
                .build();

        when(accountRepository.findById(testAccount.getId())).thenReturn(Optional.of(testAccount));
        when(accountRepository.findById(targetAccount.getId())).thenReturn(Optional.of(targetAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        accountService.transferFunds(
                testAccount.getId(),
                targetAccount.getId(),
                BigDecimal.valueOf(200)
        );

        assertEquals(BigDecimal.valueOf(800), testAccount.getBalance());
        assertEquals(BigDecimal.valueOf(700), targetAccount.getBalance());
        assertNotNull(testAccount.getLastActivityAt());
        assertNotNull(targetAccount.getLastActivityAt());

        verify(accountRepository, times(2)).save(any(Account.class));
    }

    @Test
    void transferFunds_InsufficientFunds() {
        Account targetAccount = Account.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .accountNumber("0987654321")
                .accountType(AccountType.CHECKING)
                .balance(BigDecimal.valueOf(500))
                .status(AccountStatus.ACTIVE)
                .build();

        when(accountRepository.findById(testAccount.getId())).thenReturn(Optional.of(testAccount));
        when(accountRepository.findById(targetAccount.getId())).thenReturn(Optional.of(targetAccount));

        assertThrows(InsufficientFundsException.class, () ->
                accountService.transferFunds(
                        testAccount.getId(),
                        targetAccount.getId(),
                        BigDecimal.valueOf(2000)
                ));
    }

    @Test
    void getAccountsByUserId_Success() {
        when(accountRepository.findByUserId(testUserId)).thenReturn(List.of(testAccount));

        List<Account> accounts = accountService.getAccountsByUserId(testUserId);

        assertFalse(accounts.isEmpty());
        assertEquals(1, accounts.size());
        assertEquals(testAccount.getId(), accounts.get(0).getId());
    }
}
