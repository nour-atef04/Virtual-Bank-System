package com.example.user_service.exception;

public class UserProfileNotFoundException extends BaseServiceException {

    public UserProfileNotFoundException() {
        super(ErrorType.USER_NOT_FOUND, ErrorType.USER_NOT_FOUND.getDefaultMessage());
    }

    public UserProfileNotFoundException(String customMessage) {
        super(ErrorType.USER_NOT_FOUND, customMessage);
    }
}
