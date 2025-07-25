package com.example.bff_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private String accountId;
    private String accountNumber;
    private String accountType;
    private double balance;
    private List<TransactionDto> transactions;

    public AccountDto withTransactions(List<TransactionDto> transactions) {
        return AccountDto.builder()
                .accountId(this.accountId)
                .accountNumber(this.accountNumber)
                .accountType(this.accountType)
                .balance(this.balance)
                .transactions(transactions)
                .build();
    }
}