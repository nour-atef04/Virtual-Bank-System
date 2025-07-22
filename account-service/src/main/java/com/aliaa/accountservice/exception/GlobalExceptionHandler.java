package com.aliaa.accountservice.exception;

import com.aliaa.accountservice.dto.ErrorResponse;
import com.aliaa.accountservice.logging.LoggingProducer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final LoggingProducer loggingProducer;

    public GlobalExceptionHandler(LoggingProducer loggingProducer) {
        this.loggingProducer = loggingProducer;
    }

    // Handle malformed JSON requests
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, WebRequest request) {

        String errorMessage = determineErrorMessage(request);
        ErrorResponse errorResponse = buildErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);

        logError("MALFORMED_REQUEST", errorResponse, ex);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    // Handle validation errors (@Valid failures)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        ErrorResponse errorResponse = buildErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);

        logError("VALIDATION_ERROR", errorResponse, ex);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    // Handle business logic errors (IllegalArgumentException)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex) {

        ErrorResponse errorResponse = buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());

        logError("BUSINESS_ERROR", errorResponse, ex);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    // Handle all other unexpected exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(
            Exception ex, WebRequest request) {

        ErrorResponse errorResponse = buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred");

        logError("UNEXPECTED_ERROR", errorResponse, ex);
        return ResponseEntity.internalServerError().body(errorResponse);
    }

    // --- Helper Methods ---
    private String determineErrorMessage(WebRequest request) {
        String path = request.getDescription(false);
        if (path.contains("/transfer")) {
            return "Invalid transfer request format";
        } else if (path.contains("/accounts")) {
            return "Invalid account creation request";
        }
        return "Invalid request format";
    }

    private ErrorResponse buildErrorResponse(HttpStatus status, String message) {
        return ErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private void logError(String errorType, ErrorResponse errorResponse, Exception ex) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("errorType", errorType);
        errorDetails.put("response", errorResponse);
        errorDetails.put("exception", ex.getClass().getSimpleName());
        errorDetails.put("exceptionMessage", ex.getMessage());

        loggingProducer.sendLog(errorDetails, "ERROR_" + errorType);
    }
}