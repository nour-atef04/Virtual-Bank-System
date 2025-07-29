package com.example.bff_service.exception;

import com.example.bff_service.dto.ErrorResponse;
import com.example.bff_service.logging.LoggingProducer;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final LoggingProducer loggingProducer;

    public GlobalExceptionHandler(LoggingProducer loggingProducer) {
        this.loggingProducer = loggingProducer;
    }

    @ExceptionHandler(DownstreamException.class)
    public ResponseEntity<Map<String, Object>> handleDownstream(DownstreamException ex) {
        Map<String, Object> errorBody = new LinkedHashMap<>();
        errorBody.put("status", ex.getStatus());
        errorBody.put("error", ex.getError());
        errorBody.put("message", ex.getMessage());

        return ResponseEntity.status(ex.getStatus()).body(errorBody);
    }

    @ExceptionHandler(BaseServiceException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleBaseException(BaseServiceException ex) {
        ErrorResponse error = ErrorResponse.of(ex.getErrorType(), ex.getMessage());
        loggingProducer.sendLog(error, "ERROR");
        return Mono.just(ResponseEntity.status(ex.getErrorType().getStatus()).body(error));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleOtherExceptions(Exception ex) {
        ErrorResponse error = ErrorResponse.of(ErrorType.INTERNAL_ERROR, ex.getMessage());
        loggingProducer.sendLog(error, "ERROR");
        return Mono.just(ResponseEntity.status(ErrorType.INTERNAL_ERROR.getStatus()).body(error));
    }
}
