package com.aliaa.accountservice.exception;

public class UserHasNoAccountsException extends BaseServiceException {

    public UserHasNoAccountsException() {
        super(ErrorType.USER_HAS_NO_ACCOUNTS, ErrorType.USER_HAS_NO_ACCOUNTS.getDefaultMessage());
    }

    public UserHasNoAccountsException(String customMessage) {
        super(ErrorType.USER_HAS_NO_ACCOUNTS, customMessage);
    }
}
