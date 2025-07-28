package com.aliaa.accountservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InactiveAccountException extends RuntimeException {
    public InactiveAccountException(String message){
        super(message);
    }
}
