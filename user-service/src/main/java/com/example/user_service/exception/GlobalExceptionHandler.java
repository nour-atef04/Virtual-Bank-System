package com.example.user_service.exception;

import java.util.stream.Collectors;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.user_service.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(BaseServiceException.class)
        public ResponseEntity<ErrorResponse> handleBaseException(BaseServiceException ex) {
                ErrorResponse error = ErrorResponse.of(ex.getErrorType(), ex.getMessage());
                return ResponseEntity.status(ex.getErrorType().getStatus()).body(error);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
                String message = ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(e -> e.getField() + ": " + e.getDefaultMessage())
                        .collect(Collectors.joining(", "));

                ErrorResponse error = ErrorResponse.of(ErrorType.VALIDATION_ERROR, message);
                return ResponseEntity.status(ErrorType.VALIDATION_ERROR.getStatus()).body(error);
        }

        @ExceptionHandler({ConversionFailedException.class, MethodArgumentTypeMismatchException.class})
        public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex) {
                ErrorResponse error = ErrorResponse.of(ErrorType.BAD_REQUEST, "Invalid input format: " + ex.getMessage());
                return ResponseEntity.status(ErrorType.BAD_REQUEST.getStatus()).body(error);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleOtherExceptions(Exception ex) {
                ErrorResponse error = ErrorResponse.of(ErrorType.INTERNAL_ERROR, ex.getMessage());
                return ResponseEntity.status(ErrorType.INTERNAL_ERROR.getStatus()).body(error);
        }
}
