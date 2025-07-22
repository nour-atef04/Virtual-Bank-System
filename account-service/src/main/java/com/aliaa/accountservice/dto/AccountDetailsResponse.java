
package com.aliaa.accountservice.dto;

import com.aliaa.accountservice.model.AccountStatus;
import com.aliaa.accountservice.model.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDetailsResponse {
    private UUID accountId;
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal balance;
    private AccountStatus status;
}