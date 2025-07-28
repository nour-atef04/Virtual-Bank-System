package com.aliaa.accountservice.exception;

import org.springframework.http.HttpStatus;

public enum ErrorType {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "Not Found", "User profile not found"),
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "Not Found", "Account not found"),
    USER_HAS_NO_ACCOUNTS(HttpStatus.NOT_FOUND, "Not Found", "User has no accounts"),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Validation Error", "Invalid request data"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad Request", "Invalid input format"),
    WEB_CLIENT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "WebClient Error", "Failed to call remote service"),
    INSUFFICIENT_FUNDS(HttpStatus.BAD_REQUEST, "Bad Request", "Insufficient funds"),
    INACTIVE_ACCOUNT(HttpStatus.BAD_REQUEST, "Bad Request", "Inactive account"),
    INVALID_ACCOUNT_CREATION(HttpStatus.BAD_REQUEST, "Bad Request", "Invalid account creation request"),
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
