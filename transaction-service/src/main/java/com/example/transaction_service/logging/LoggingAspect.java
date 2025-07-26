package com.example.transaction_service.logging;

import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.handler.annotation.support.MethodArgumentTypeMismatchException;
import org.springframework.stereotype.Component;

import com.example.transaction_service.exceptions.AccountNotFoundException;
import com.example.transaction_service.exceptions.InsufficientBalanceException;
import com.example.transaction_service.exceptions.InvalidTransactionStateException;
import com.example.transaction_service.exceptions.TransactionNotFoundException;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    private final LoggingProducer loggingProducer;

    public LoggingAspect(LoggingProducer loggingProducer) {
        this.loggingProducer = loggingProducer;
    }

    @Before("execution(* com.example.transaction_service.controller..*.*(..))")
    public void logBeforeRequest(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            loggingProducer.sendLog(args[0], "REQUEST");
        }
    }

    @AfterReturning(pointcut = "execution(* com.example.transaction_service.controller..*.*(..))", returning = "result")
    public void logAfterResponse(JoinPoint joinPoint, Object result) {
        Object responseBody = result;

        // If it's a ResponseEntity, extract the body
        if (result instanceof ResponseEntity) {
            ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;
            responseBody = responseEntity.getBody();
        }

        loggingProducer.sendLog(responseBody, "RESPONSE");
    }

    @AfterThrowing(pointcut = "execution(* com.example.transaction_service.controller..*.*(..))", throwing = "ex")
    public void logAfterException(JoinPoint joinPoint, Throwable ex) {
        Map<String, Object> errorLog = new LinkedHashMap<>();

        if (ex instanceof com.example.transaction_service.exceptions.TransactionNotFoundException) {
            errorLog.put("status", 404);
            errorLog.put("error", "Not Found");
        } else if (ex instanceof AccountNotFoundException ||
                ex instanceof InsufficientBalanceException ||
                ex instanceof InvalidTransactionStateException ||
                ex instanceof MethodArgumentTypeMismatchException
                ||
                ex instanceof org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException) {
            errorLog.put("status", 400);
            errorLog.put("error", "Bad Request");
        } else {
            errorLog.put("status", 500);
            errorLog.put("error", "Internal Server Error");
        }

        errorLog.put("message", ex.getMessage());
        loggingProducer.sendLog(errorLog, "ERROR");
    }

}
