package com.example.user_service.exception;

public class InvalidCredentialsException extends BaseServiceException {

    public InvalidCredentialsException() {
        super(ErrorType.INVALID_CREDENTIALS, ErrorType.INVALID_CREDENTIALS.getDefaultMessage());
    }

    public InvalidCredentialsException(String customMessage) {
        super(ErrorType.INVALID_CREDENTIALS, customMessage);
    }
}
