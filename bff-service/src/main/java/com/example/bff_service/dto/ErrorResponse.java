package com.example.bff_service.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
public class ErrorResponse {
    private final int status;
    private final String error;
    private final String message;
    private final Instant timestamp;
    private Map<String, String> details;

    public ErrorResponse(int status, String error, String message, Instant timestamp, Map<String, String> details) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = timestamp;
        this.details = details;
    }

    public ErrorResponse(int status, String error, String message, Instant timestamp) {
        this(status, error, message, timestamp, null);
    }

}