package com.example.bff_service.exception;

public class UserNotFoundException extends BaseServiceException {
    public UserNotFoundException(String userId) {
        super(ErrorType.NOT_FOUND, "User not found with ID: " + userId);
    }
}