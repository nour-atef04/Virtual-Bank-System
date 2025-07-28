package com.aliaa.accountservice.exception;

public class AccountNotFoundException extends BaseServiceException {

    public AccountNotFoundException() {
        super(ErrorType.ACCOUNT_NOT_FOUND, ErrorType.ACCOUNT_NOT_FOUND.getDefaultMessage());
    }

    public AccountNotFoundException(String customMessage) {
        super(ErrorType.ACCOUNT_NOT_FOUND, customMessage);
    }
}
