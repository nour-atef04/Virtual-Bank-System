package com.example.user_service.exception;

import java.util.stream.Collectors;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException;

import com.example.user_service.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

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
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(errorResponse);

        }

        // Handles user already exists
        @ExceptionHandler(UserAlreadyExistsException.class)
        public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException e) {
                ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.value(), "Conflict",
                                e.getMessage());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        // Handles invalid credentials
        @ExceptionHandler(InvalidCredentialsException.class)
        public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException e) {
                ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Unauthorized",
                                e.getMessage());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        // Handles profile not found
        @ExceptionHandler(UserProfileNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleUserProfileNotFound(UserProfileNotFoundException e) {
                ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Not Found",
                                e.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        // Handles invalid input format
        @ExceptionHandler({ ConversionFailedException.class, MethodArgumentTypeMismatchException.class })
        public ResponseEntity<ErrorResponse> handleConversionErrors(Exception ex) {
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Bad Request",
                                "Invalid input format: " + ex.getMessage());

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        @ExceptionHandler(MethodArgumentConversionNotSupportedException.class)
        public ResponseEntity<ErrorResponse> handleConversionNotSupported(
                        MethodArgumentConversionNotSupportedException ex) {
                ErrorResponse error = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Bad Request",
                                "Invalid UUID format: " + ex.getValue());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        

        // For other unhandled exceptions
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGeneral(Exception e) {

                ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                "Internal Server Error", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(errorResponse);

        }

}
