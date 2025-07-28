package com.example.bff_service.dto;

import com.example.bff_service.exception.ErrorType;
import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class ErrorResponse {
    private final int status;
    private final String error;
    private final String message;
    private final Map<String, String> validationErrors;

    public ErrorResponse(int status, String error, String message) {
        this(status, error, message, null);
    }

    public ErrorResponse(int status, String error, String message, Map<String, String> validationErrors) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.validationErrors = validationErrors;
    }

    public static ErrorResponse of(ErrorType type, String message) {
        return new ErrorResponse(type.getStatus().value(), type.getTitle(), message);
    }

    public static ErrorResponse of(ErrorType type) {
        return of(type, type.getDefaultMessage());
    }

    public static ErrorResponse of(ErrorType type, String message, Map<String, String> validationErrors) {
        return new ErrorResponse(type.getStatus().value(), type.getTitle(), message, validationErrors);
    }
}

