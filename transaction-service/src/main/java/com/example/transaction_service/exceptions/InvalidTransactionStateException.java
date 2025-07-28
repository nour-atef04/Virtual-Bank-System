package com.example.transaction_service.exceptions;

public class InvalidTransactionStateException extends BaseServiceException {

    public InvalidTransactionStateException() {
        super(ErrorType.INVALID_TRANSACTION_STATE, ErrorType.INVALID_TRANSACTION_STATE.getDefaultMessage());
    }

    public InvalidTransactionStateException(String customMessage) {
        super(ErrorType.INVALID_TRANSACTION_STATE, customMessage);
    }
}
