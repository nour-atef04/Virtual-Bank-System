package com.example.user_service.exception;


public class UserAlreadyExistsException extends BaseServiceException {

    public UserAlreadyExistsException() {
        super(ErrorType.USER_ALREADY_EXISTS, ErrorType.USER_ALREADY_EXISTS.getDefaultMessage());
    }

    public UserAlreadyExistsException(String customMessage) {
        super(ErrorType.USER_ALREADY_EXISTS, customMessage);
    }
}
