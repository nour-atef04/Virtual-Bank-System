package com.example.bff_service.service;

import com.example.bff_service.dto.DashboardResponse;
import reactor.core.publisher.Mono;

public interface DashboardService {

    public Mono<DashboardResponse> getDashboardData(String userId);
}
