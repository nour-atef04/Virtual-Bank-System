package com.example.transaction_service.exceptions;

import org.springframework.http.HttpStatus;

public enum ErrorType {
    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "Bad Request", "Insufficient balance"),
    INVALID_TRANSACTION_STATE(HttpStatus.BAD_REQUEST, "Bad Request", "Invalid transaction state"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Unauthorized", "Invalid credentials"),
    TRANSACTIONS_NOT_FOUND(HttpStatus.NOT_FOUND, "Not Found", "Transactions not found"),
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "Not Found", "Account not found"),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Validation Error", "Invalid request data"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad Request", "Invalid input format"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Unexpected error"),
    SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Service Error", "An error occurred in the service"),
    DOWNSTREAM_SERVICE_ERROR(HttpStatus.BAD_GATEWAY, "Downstream Service Error",
            "Error communicating with downstream service");

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
