package com.example.transaction_service.exceptions;

public class InsufficientBalanceException extends BaseServiceException {

    public InsufficientBalanceException() {
        super(ErrorType.INSUFFICIENT_BALANCE, ErrorType.INSUFFICIENT_BALANCE.getDefaultMessage());
    }

    public InsufficientBalanceException(String customMessage) {
        super(ErrorType.INSUFFICIENT_BALANCE, customMessage);
    }
}
