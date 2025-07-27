package com.example.bff_service.controller;

import com.example.bff_service.dto.DashboardResponse;
import com.example.bff_service.dto.ErrorResponse;
import com.example.bff_service.exception.ServiceException;
import com.example.bff_service.exception.UserNotFoundException;
import com.example.bff_service.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Instant;

@RestController
@RequestMapping("/bff")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/dashboard/{userId}")
    public Mono<ResponseEntity<?>> getDashboard(@PathVariable String userId) {
        Mono<DashboardResponse> cachedResponse = dashboardService.getDashboardData(userId).cache();

        return cachedResponse
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .onErrorResume(UserNotFoundException.class, e ->
                        Mono.just(ResponseEntity.notFound().build()))
                .onErrorResume(ServiceException.class, e ->
                        Mono.just(ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ErrorResponse.builder()
                                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                        .error("Internal Server Error")
                                        .message("Failed to retrieve dashboard data: " + e.getMessage())
                                        .timestamp(Instant.now())
                                        .build())));
    }
}