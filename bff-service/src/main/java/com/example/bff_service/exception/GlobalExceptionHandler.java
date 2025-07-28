package com.example.bff_service.exception;

import com.example.bff_service.dto.ErrorResponse;
import com.example.bff_service.logging.LoggingProducer;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final LoggingProducer loggingProducer;

    public GlobalExceptionHandler(LoggingProducer loggingProducer) {
        this.loggingProducer = loggingProducer;
    }

    @ExceptionHandler(BaseServiceException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleBaseException(BaseServiceException ex) {
        ErrorResponse error = ErrorResponse.of(ex.getErrorType(), ex.getMessage());
        loggingProducer.sendLog(error, "ERROR");
        return Mono.just(ResponseEntity.status(ex.getErrorType().getStatus()).body(error));
    }

    @ExceptionHandler({WebExchangeBindException.class, MethodArgumentNotValidException.class})
    public Mono<ResponseEntity<ErrorResponse>> handleValidation(Exception ex) {
        Map<String, String> errors = (ex instanceof WebExchangeBindException)
                ? ((WebExchangeBindException) ex).getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField(),
                        fieldError -> fieldError.getDefaultMessage()))
                : ((MethodArgumentNotValidException) ex).getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField(),
                        fieldError -> fieldError.getDefaultMessage()));

        ErrorResponse error = ErrorResponse.of(ErrorType.VALIDATION_ERROR, "Validation failed", errors);
        loggingProducer.sendLog(error, "ERROR");
        return Mono.just(ResponseEntity.status(ErrorType.VALIDATION_ERROR.getStatus()).body(error));
    }

    @ExceptionHandler(ConversionFailedException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleBadRequest(Exception ex) {
        ErrorResponse error = ErrorResponse.of(ErrorType.BAD_REQUEST, "Invalid input: " + ex.getMessage());
        loggingProducer.sendLog(error, "ERROR");
        return Mono.just(ResponseEntity.status(ErrorType.BAD_REQUEST.getStatus()).body(error));
    }

    @ExceptionHandler(WebClientResponseException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleWebClientException(WebClientResponseException ex) {
        ErrorType errorType = ex.getStatusCode().is4xxClientError()
                ? ErrorType.BAD_REQUEST
                : ErrorType.DOWNSTREAM_ERROR;

        ErrorResponse error = ErrorResponse.of(errorType, "Downstream service error: " + ex.getMessage());
        loggingProducer.sendLog(error, "ERROR");
        return Mono.just(ResponseEntity.status(errorType.getStatus()).body(error));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleOtherExceptions(Exception ex) {
        ErrorResponse error = ErrorResponse.of(ErrorType.INTERNAL_ERROR, ex.getMessage());
        loggingProducer.sendLog(error, "ERROR");
        return Mono.just(ResponseEntity.status(ErrorType.INTERNAL_ERROR.getStatus()).body(error));
    }
}
