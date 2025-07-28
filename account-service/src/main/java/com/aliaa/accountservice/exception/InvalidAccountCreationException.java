package com.aliaa.accountservice.exception;

public class InvalidAccountCreationException extends BaseServiceException {

    public InvalidAccountCreationException() {
        super(ErrorType.INVALID_ACCOUNT_CREATION, ErrorType.INVALID_ACCOUNT_CREATION.getDefaultMessage());
    }

    public InvalidAccountCreationException(String customMessage) {
        super(ErrorType.INVALID_ACCOUNT_CREATION, customMessage);
    }
}
