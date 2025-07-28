package com.example.transaction_service.dto;

import com.example.transaction_service.exceptions.ErrorType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String error;
    private String message;

    public static ErrorResponse of(ErrorType type, String message) {
        return new ErrorResponse(type.getStatus().value(), type.getTitle(), message);
    }

    public static ErrorResponse of(ErrorType type) {
        return of(type, type.getDefaultMessage());
    }
}
