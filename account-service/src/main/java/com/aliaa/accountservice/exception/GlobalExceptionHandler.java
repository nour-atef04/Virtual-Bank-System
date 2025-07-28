package com.aliaa.accountservice.exception;

import com.aliaa.accountservice.dto.ErrorResponse;
import com.aliaa.accountservice.logging.LoggingProducer;

import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestControllerAdvice
public class GlobalExceptionHandler {

        private final LoggingProducer loggingProducer;

        public GlobalExceptionHandler(LoggingProducer loggingProducer) {
                this.loggingProducer = loggingProducer;
        }

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
                loggingProducer.sendLog(errorResponse, "ERROR");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(errorResponse);

        }

        // Handles invalid input format
        @ExceptionHandler({ ConversionFailedException.class, MethodArgumentTypeMismatchException.class })
        public ResponseEntity<ErrorResponse> handleConversionErrors(Exception ex) {
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Bad Request",
                                "Invalid input format: " + ex.getMessage());
                loggingProducer.sendLog(errorResponse, "ERROR");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        @ExceptionHandler(WebClientResponseException.class)
        public Mono<ResponseEntity<ErrorResponse>> handleWebClientException(WebClientResponseException ex) {
                HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value()) != null
                                ? HttpStatus.resolve(ex.getStatusCode().value())
                                : HttpStatus.INTERNAL_SERVER_ERROR;

                ErrorResponse errorResponse = new ErrorResponse(
                                status.value(),
                                "WebClient Error",
                                ex.getResponseBodyAsString());
                loggingProducer.sendLog(errorResponse, "ERROR");
                return Mono.just(ResponseEntity.status(status).body(errorResponse));
        }

        // Handles account not found
        @ExceptionHandler(AccountNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleUserProfileNotFound(AccountNotFoundException e) {
                ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Not Found",
                                e.getMessage());
                loggingProducer.sendLog(errorResponse, "ERROR");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        // No accounts found for user
        @ExceptionHandler(UserHasNoAccountsException.class)
        public ResponseEntity<ErrorResponse> handleUserHasNoAccounts(UserHasNoAccountsException ex) {
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.NOT_FOUND.value(),
                                "Not Found",
                                ex.getMessage());
                loggingProducer.sendLog(errorResponse, "ERROR");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        // User not found
        @ExceptionHandler(UserProfileNotFoundException.class)
        public ResponseEntity<ErrorResponse> hProfileNotFoundException(UserProfileNotFoundException ex) {
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.NOT_FOUND.value(),
                                "Not Found",
                                ex.getMessage());
                loggingProducer.sendLog(errorResponse, "ERROR");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        // Invalid account type or balance in create request
        @ExceptionHandler(InvalidAccountCreationException.class)
        public ResponseEntity<ErrorResponse> handleInvalidAccountCreation(InvalidAccountCreationException ex) {
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Bad Request",
                                ex.getMessage());
                loggingProducer.sendLog(errorResponse, "ERROR");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        // Insuffient funds when transferring
        @ExceptionHandler(InsufficientFundsException.class)
        public ResponseEntity<ErrorResponse> handleInsufficientFundsException(InsufficientFundsException ex) {
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Bad Request",
                                ex.getMessage());
                loggingProducer.sendLog(errorResponse, "ERROR");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        // Inactive accounts when transferring
        @ExceptionHandler(InactiveAccountException.class)
        public ResponseEntity<ErrorResponse> handleInactiveAccountException(InactiveAccountException ex) {
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Bad Request",
                                ex.getMessage());
                loggingProducer.sendLog(errorResponse, "ERROR");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        // For other unhandled exceptions
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGeneral(Exception e) {

                ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                "Internal Server Error", e.getMessage());
                loggingProducer.sendLog(errorResponse, "ERROR");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(errorResponse);

        }

}