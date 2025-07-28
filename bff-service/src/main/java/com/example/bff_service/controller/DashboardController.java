package com.example.bff_service.controller;

import com.example.bff_service.dto.DashboardResponse;
import com.example.bff_service.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/bff")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/dashboard/{userId}")
    public Mono<ResponseEntity<?>> getDashboard(@PathVariable String userId) {
        Mono<DashboardResponse> cachedResponse = dashboardService.getDashboardData(userId).cache();

        return cachedResponse.map(ResponseEntity::ok);
    }
}