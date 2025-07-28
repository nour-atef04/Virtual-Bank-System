package com.aliaa.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;


import com.aliaa.accountservice.exception.ErrorType;

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