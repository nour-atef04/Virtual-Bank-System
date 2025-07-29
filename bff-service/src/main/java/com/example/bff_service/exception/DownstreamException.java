package com.example.bff_service.exception;

import org.springframework.http.HttpStatusCode;

public class DownstreamException extends RuntimeException {
    private final int status;
    private final String error;
    private final String message;

    public DownstreamException(String message, HttpStatusCode statusCode, String error) {
        super(message);
        this.status = statusCode.value();
        this.error = error;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

