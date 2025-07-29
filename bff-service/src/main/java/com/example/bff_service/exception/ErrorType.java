package com.example.bff_service.exception;

import org.springframework.http.HttpStatus;

public enum ErrorType {

    SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Service Error", "An error occurred in the service"),
    DOWNSTREAM_SERVICE_ERROR(HttpStatus.BAD_GATEWAY, "Downstream Service Error", "Error communicating with downstream service"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Unexpected error occurred");

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
