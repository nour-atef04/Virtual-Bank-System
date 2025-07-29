package com.example.bff_service.controller;

import com.example.bff_service.client.TransactionServiceClient;
import com.example.bff_service.dto.AppNameWrappedResponse;
import com.example.bff_service.dto.TransactionExecutionRequest;
import com.example.bff_service.dto.TransactionExecutionResponse;
import com.example.bff_service.dto.TransactionInitiationRequest;
import com.example.bff_service.dto.TransactionInitiationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

// @RestController
// @RequestMapping("/transactions")
// @RequiredArgsConstructor
// public class TransactionController {
//     private final TransactionServiceClient transactionServiceClient;

//     @PostMapping("/transfer/initiation")
//     public Mono<ResponseEntity<TransactionInitiationResponse>> initiateTransfer(
//             @RequestBody TransactionInitiationRequest request) {
//         return transactionServiceClient.initiateTransfer(request)
//                 .map(ResponseEntity::ok);
//     }

//     @PostMapping("/transfer/execution")
//     public Mono<ResponseEntity<TransactionExecutionResponse>> executeTransfer(
//             @RequestBody TransactionExecutionRequest request) {
//         return transactionServiceClient.executeTransfer(request)
//                 .map(ResponseEntity::ok);
//     }
// }

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionServiceClient transactionServiceClient;

    @PostMapping("/transfer/initiation")
    public Mono<ResponseEntity<AppNameWrappedResponse<?>>> initiateTransfer(
            @RequestBody TransactionInitiationRequest request,
            @RequestHeader("APP-NAME") String appName) {

        return transactionServiceClient.initiateTransfer(request)
                .map(response -> {
                    AppNameWrappedResponse<TransactionInitiationResponse> wrapped = new AppNameWrappedResponse<>(
                            appName, response);
                    return ResponseEntity.ok(wrapped);
                });
    }

    @PostMapping("/transfer/execution")
    public Mono<ResponseEntity<AppNameWrappedResponse<?>>> executeTransfer(
            @RequestBody TransactionExecutionRequest request,
            @RequestHeader("APP-NAME") String appName) {

        return transactionServiceClient.executeTransfer(request)
                .map(response -> {
                    AppNameWrappedResponse<TransactionExecutionResponse> wrapped = new AppNameWrappedResponse<>(appName,
                            response);
                    return ResponseEntity.ok(wrapped);
                });
    }
}
