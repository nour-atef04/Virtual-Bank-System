package com.example.transaction_service.exceptions;

public abstract class BaseServiceException extends RuntimeException {
    private final ErrorType errorType;

    public BaseServiceException(ErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }
}
