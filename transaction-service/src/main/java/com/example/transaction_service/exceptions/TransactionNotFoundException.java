package com.example.transaction_service.exceptions;

public class TransactionNotFoundException extends BaseServiceException {

    public TransactionNotFoundException() {
        super(ErrorType.TRANSACTIONS_NOT_FOUND, ErrorType.TRANSACTIONS_NOT_FOUND.getDefaultMessage());
    }

    public TransactionNotFoundException(String customMessage) {
        super(ErrorType.TRANSACTIONS_NOT_FOUND, customMessage);
    }
}
