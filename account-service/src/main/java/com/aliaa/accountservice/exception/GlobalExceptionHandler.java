package com.aliaa.accountservice.exception;

import java.util.stream.Collectors;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.messaging.handler.annotation.support.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.aliaa.accountservice.dto.ErrorResponse;
import com.aliaa.accountservice.logging.LoggingProducer;

@RestControllerAdvice
public class GlobalExceptionHandler {

        private final LoggingProducer loggingProducer;

        public GlobalExceptionHandler(LoggingProducer loggingProducer) {
                this.loggingProducer = loggingProducer;
        }

        @ExceptionHandler(BaseServiceException.class)
        public ResponseEntity<ErrorResponse> handleBaseException(BaseServiceException ex) {
                ErrorResponse error = ErrorResponse.of(ex.getErrorType(), ex.getMessage());
                loggingProducer.sendLog(error, "ERROR");
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
                loggingProducer.sendLog(error, "ERROR");
                return ResponseEntity.status(ErrorType.VALIDATION_ERROR.getStatus()).body(error);
        }

        @ExceptionHandler({ ConversionFailedException.class, MethodArgumentTypeMismatchException.class,
                        HttpMessageNotReadableException.class })
        public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex) {
                String rootMessage = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                ErrorResponse error = ErrorResponse.of(ErrorType.BAD_REQUEST, "Invalid input format: " + rootMessage);
                loggingProducer.sendLog(error, "ERROR");
                return ResponseEntity.status(ErrorType.BAD_REQUEST.getStatus()).body(error);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleOtherExceptions(Exception ex) {
                ErrorResponse error = ErrorResponse.of(ErrorType.INTERNAL_ERROR, ex.getMessage());
                loggingProducer.sendLog(error, "ERROR");
                return ResponseEntity.status(ErrorType.INTERNAL_ERROR.getStatus()).body(error);
        }
}
