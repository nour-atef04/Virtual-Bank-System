package com.aliaa.accountservice.exception;

public class InsufficientFundsException extends BaseServiceException {

    public InsufficientFundsException() {
        super(ErrorType.INSUFFICIENT_FUNDS, ErrorType.INSUFFICIENT_FUNDS.getDefaultMessage());
    }

    public InsufficientFundsException(String customMessage) {
        super(ErrorType.INSUFFICIENT_FUNDS, customMessage);
    }
}
