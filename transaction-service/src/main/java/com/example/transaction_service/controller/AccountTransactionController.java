// package com.example.transaction_service.controller;

// import java.util.List;
// import java.util.UUID;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.example.transaction_service.dto.TransactionDetail;
// import com.example.transaction_service.service.TransactionService;

// @RestController
// @RequestMapping("/accounts")
// public class AccountTransactionController {

//     @Autowired
//     private TransactionService transactionService;

//     @GetMapping("/{accountId}/transactions")
//     public ResponseEntity<List<TransactionDetail>> getTransactionsForAccount(@PathVariable UUID accountId) {

//         System.out.println("Received get account request: " + accountId);

//         List<TransactionDetail> transactions = transactionService.getTransactionsForAccount(accountId);

//         System.out.println("Returning response: " + transactions);

//         return ResponseEntity.ok(transactions);

//     }

// }

package com.example.transaction_service.controller;

import com.example.transaction_service.dto.TransactionDetail;
import com.example.transaction_service.service.TransactionService;

import reactor.core.publisher.Mono;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountTransactionController {

    private final TransactionService transactionService;

    public AccountTransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // @GetMapping("/{accountId}/transactions")
    // public ResponseEntity<?> getTransactionsForAccount(@PathVariable UUID
    // accountId) {
    // List<TransactionDetail> transactions =
    // transactionService.getTransactionsForAccount(accountId)
    // .collectList()
    // .block();

    // return ResponseEntity.ok(transactions);
    // }

    @GetMapping("/{accountId}/transactions")
    public Mono<ResponseEntity<List<TransactionDetail>>> getTransactionsForAccount(@PathVariable UUID accountId) {
        return transactionService.getTransactionsForAccount(accountId)
                .collectList()
                .map(list -> ResponseEntity.ok(list));
    }
}
