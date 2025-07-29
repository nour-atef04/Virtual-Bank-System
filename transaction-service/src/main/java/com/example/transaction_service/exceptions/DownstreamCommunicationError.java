package com.example.transaction_service.exceptions;

public class DownstreamCommunicationError extends BaseServiceException {
    public DownstreamCommunicationError(String message) {
        super(ErrorType.DOWNSTREAM_SERVICE_ERROR, message);
    }

    public DownstreamCommunicationError(ErrorType errorType, String message) {
        super(errorType, message);
    }
}
