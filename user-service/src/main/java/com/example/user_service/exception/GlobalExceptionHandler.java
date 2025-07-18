package com.example.user_service.exception;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.user_service.dto.ErrorResponse;
import com.example.user_service.logging.LoggingProducer;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private LoggingProducer loggingProducer;

    // Handling validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException e) {

        String errorMessage = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation Error",
                errorMessage);
        loggingProducer.sendLog(errorResponse, "Error");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);

    }

    // Handles IllegalArgumentException (user already exists)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.value(), "Conflict", e.getMessage());
        loggingProducer.sendLog(errorResponse, "Error");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(errorResponse);

    }

    // Handles IllegalAccessException (wrong credentials at login)
    @ExceptionHandler(IllegalAccessException.class)
    public ResponseEntity<ErrorResponse> handleIllegalException(IllegalAccessException e) {

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Unauthorized",
                e.getMessage());
        loggingProducer.sendLog(errorResponse, "Error");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(errorResponse);

    }

    // Handles EntityNotFoundException (profile not found)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException e) {

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Not Found", e.getMessage());
        loggingProducer.sendLog(errorResponse, "Error");
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorResponse);

    }

    // For other unhandled exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception e) {

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error", e.getMessage());
        loggingProducer.sendLog(errorResponse, "Error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);

    }

}
