package com.example.user_service.exception;

import org.springframework.http.HttpStatus;

public enum ErrorType {
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "Conflict", "User already exists"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Unauthorized", "Invalid credentials"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "Not Found", "User profile not found"),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Validation Error", "Invalid request data"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad Request", "Invalid input format"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Unexpected error");

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
