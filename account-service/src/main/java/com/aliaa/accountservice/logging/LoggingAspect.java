package com.aliaa.accountservice.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

import reactor.core.publisher.Mono;

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

    @AfterReturning(pointcut = "execution(* com.aliaa.accountservice.controller..*.*(..))", returning = "result")
    public void logAfterSuccess(Object result) {
        if (result instanceof Mono) {
            ((Mono<?>) result)
                    .doOnSuccess(response -> {
                        Object responseBody = response instanceof ResponseEntity
                                ? ((ResponseEntity<?>) response).getBody()
                                : response;
                        loggingProducer.sendLog(
                                responseBody != null ? responseBody : Collections.singletonMap("response", "null"),
                                "RESPONSE");
                    })
                    .subscribe();
        } else {
            Object responseBody = result;
            if (result instanceof ResponseEntity) {
                responseBody = ((ResponseEntity<?>) result).getBody();
            }
            if (responseBody != null) {
                loggingProducer.sendLog(responseBody, "RESPONSE");
            } else {
                loggingProducer.sendLog(Collections.singletonMap("response", "null"), "RESPONSE");
            }
        }
    }

}