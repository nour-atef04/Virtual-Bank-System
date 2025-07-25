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
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final LoggingProducer loggingProducer;

    public GlobalExceptionHandler(LoggingProducer loggingProducer) {
        this.loggingProducer = loggingProducer;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(WebRequest request) {
        String errorMessage = determineErrorMessage(request);
        ErrorResponse errorResponse = buildErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
        loggingProducer.sendLog(errorResponse, "ERROR");
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        ErrorResponse errorResponse = buildErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
        loggingProducer.sendLog(errorResponse, "ERROR");
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ErrorResponse> handleWebClientException(WebClientResponseException ex) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value()) != null
                ? HttpStatus.resolve(ex.getStatusCode().value())
                : HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse errorResponse = buildErrorResponse(
                status,
                ex.getStatusText());

        loggingProducer.sendLog(errorResponse, "ERROR");
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        HttpStatus status = isNotFoundException(ex) ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        ErrorResponse errorResponse = buildErrorResponse(status, ex.getMessage());
        loggingProducer.sendLog(errorResponse, "ERROR");
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions() {
        ErrorResponse errorResponse = buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred");
        loggingProducer.sendLog(errorResponse, "ERROR");
        return ResponseEntity.internalServerError().body(errorResponse);
    }

    private boolean isNotFoundException(IllegalArgumentException ex) {
        if (ex.getMessage() == null) {
            return false;
        }
        String lowerCaseMsg = ex.getMessage().toLowerCase();
        return lowerCaseMsg.contains("not found") || lowerCaseMsg.contains("no accounts found");
    }




    private String determineErrorMessage(WebRequest request) {
        String path = request.getDescription(false);
        if (path.contains("/transfer")) {
            return "Invalid transfer request format";
        } else if (path.contains("/users")){
            return "No accounts found";
        }else if (path.contains("/accounts")) {
            return "Invalid account type or initial balance";
        }

        return "Invalid request format";
    }

    private ErrorResponse buildErrorResponse(HttpStatus status, String message) {
        return ErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .build();
    }
}