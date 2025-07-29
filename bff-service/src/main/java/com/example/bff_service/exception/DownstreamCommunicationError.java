package com.example.bff_service.exception;

public class DownstreamCommunicationError extends BaseServiceException {
    public DownstreamCommunicationError(String message) {
        super(ErrorType.DOWNSTREAM_SERVICE_ERROR, message);
    }

    public DownstreamCommunicationError(ErrorType errorType, String message) {
        super(errorType, message);
    }
}
