package com.example.bff_service.exception;

public class ServiceException extends BaseServiceException {
    public ServiceException(String message) {
        super(ErrorType.SERVICE_ERROR, message);
    }

    public ServiceException(ErrorType errorType, String message) {
        super(errorType, message);
    }
}