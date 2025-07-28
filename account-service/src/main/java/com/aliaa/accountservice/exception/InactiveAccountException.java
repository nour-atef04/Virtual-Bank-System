package com.aliaa.accountservice.exception;

public class InactiveAccountException extends BaseServiceException {

    public InactiveAccountException() {
        super(ErrorType.INACTIVE_ACCOUNT, ErrorType.INACTIVE_ACCOUNT.getDefaultMessage());
    }

    public InactiveAccountException(String customMessage) {
        super(ErrorType.INACTIVE_ACCOUNT, customMessage);
    }
}
