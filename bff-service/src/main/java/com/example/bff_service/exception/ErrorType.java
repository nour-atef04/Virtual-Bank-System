package com.example.bff_service.exception;

import org.springframework.http.HttpStatus;

public enum ErrorType {
    SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Service Error", "An error occurred in the service"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "Not Found", "User not found"),
    DOWNSTREAM_SERVICE_ERROR(HttpStatus.BAD_GATEWAY, "Downstream Service Error", "Error communicating with downstream service"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Unexpected error occurred"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad Request", "Invalid input"),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Validation Error", "Request validation failed"),
    DOWNSTREAM_ERROR(HttpStatus.BAD_GATEWAY, "Downstream Error", "Failed to communicate with downstream service");

    private final HttpStatus status;
    private final String title;
    private final String defaultMessage;

    ErrorType(HttpStatus status, String title, String defaultMessage) {
        this.status = status;
        this.title = title;
        this.defaultMessage = defaultMessage;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
