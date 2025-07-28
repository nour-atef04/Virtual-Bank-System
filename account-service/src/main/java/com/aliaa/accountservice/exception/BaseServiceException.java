package com.aliaa.accountservice.exception;

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
