package com.aliaa.accountservice.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
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

    @Before("execution(* com.aliaa.accountservice.controller..*.*(..))")
    public void logBeforeRequest(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {

            loggingProducer.sendLog(args[0], "REQUEST");
        }
    }

    // In LoggingAspect.java
    @AfterReturning(pointcut = "execution(* com.aliaa.accountservice.controller..*.*(..))",
            returning = "result")
    public void logAfterSuccess(Object result) {
        System.out.println("logAfterSuccess called with result: " + result); // Debug print
        if (result != null) {
            loggingProducer.sendLog(result, "RESPONSE");

        } else {
            loggingProducer.sendLog(Collections.singletonMap("response", "null"), "RESPONSE");
        }
    }

    @AfterThrowing(pointcut = "execution(* com.aliaa.accountservice.controller..*.*(..))",
            throwing = "ex")
    public void logAfterException(Exception ex) {
        Map<String, Object> errorContent = new LinkedHashMap<>();
        HttpStatus status = determineHttpStatus(ex);

        errorContent.put("status", status.value());
        errorContent.put("error", status.getReasonPhrase());

        if (ex instanceof MethodArgumentNotValidException) {
            String errorMsg = ((MethodArgumentNotValidException) ex).getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                    .findFirst()
                    .orElse("Validation failed");
            errorContent.put("message", errorMsg);
        } else {
            errorContent.put("message", ex.getMessage());
        }

        loggingProducer.sendLog(errorContent, "ERROR");
    }

    private HttpStatus determineHttpStatus(Exception ex) {
        if (ex instanceof MethodArgumentNotValidException) {
            return HttpStatus.BAD_REQUEST;
        }
        if (ex instanceof HttpMessageNotReadableException) {
            return HttpStatus.BAD_REQUEST;
        }
        if (ex instanceof IllegalArgumentException) {
            if (ex.getMessage() != null && ex.getMessage().contains("not found")) {
                return HttpStatus.NOT_FOUND;
            }
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}