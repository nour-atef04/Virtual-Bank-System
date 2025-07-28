package com.example.transaction_service.logging;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
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

    @Around("execution(* com.example.transaction_service.controller..*.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] != null) {
            loggingProducer.sendLog(args[0], "REQUEST");
        }
        Object result = joinPoint.proceed();
        if (result instanceof Mono) {
            return ((Mono<?>) result)
                .doOnSuccess(response -> logResponse(response))
                .doOnError(error -> log.error("Error in controller method", error));
        } else if (result instanceof Flux) {
            return ((Flux<?>) result)
                .collectList()
                .doOnSuccess(responses -> responses.forEach(this::logResponse))
                .doOnError(error -> log.error("Error in controller method", error))
                .flatMapMany(Flux::fromIterable);
        }

        logResponse(result);
        return result;
    }

    private void logResponse(Object response) {
        Object responseBody = response;
        if (response instanceof ResponseEntity) {
            responseBody = ((ResponseEntity<?>) response).getBody();
        }

        if (responseBody != null) {
            loggingProducer.sendLog(responseBody, "RESPONSE");
        } else {
            loggingProducer.sendLog(Collections.singletonMap("response", "null"), "RESPONSE");
        }
    }
}

// @Aspect
// @Component
// @Slf4j
// public class LoggingAspect {

//     private final LoggingProducer loggingProducer;

//     public LoggingAspect(LoggingProducer loggingProducer) {
//         this.loggingProducer = loggingProducer;
//     }

//     @Before("execution(* com.example.transaction_service.controller..*.*(..)) ")
//     public void logBeforeRequest(JoinPoint joinPoint) {
//         Object[] args = joinPoint.getArgs();
//         if (args.length > 0) {
//             loggingProducer.sendLog(args[0], "REQUEST");
//         }
//     }

//     @AfterReturning(pointcut = "execution(* com.example.transaction_service.controller..*.*(..))", returning = "result")
//     public void logAfterSuccess(Object result) {
//         if (result instanceof Mono) {
//             ((Mono<?>) result)
//                     .doOnSuccess(response -> {
//                         Object responseBody = response instanceof ResponseEntity
//                                 ? ((ResponseEntity<?>) response).getBody()
//                                 : response;
//                         loggingProducer.sendLog(
//                                 responseBody != null ? responseBody : Collections.singletonMap("response", "null"),
//                                 "RESPONSE");
//                     })
//                     .subscribe();
//         } else {
//             Object responseBody = result;
//             if (result instanceof ResponseEntity) {
//                 responseBody = ((ResponseEntity<?>) result).getBody();
//             }
//             if (responseBody != null) {
//                 loggingProducer.sendLog(responseBody, "RESPONSE");
//             } else {
//                 loggingProducer.sendLog(Collections.singletonMap("response", "null"), "RESPONSE");
//             }
//         }
//     }

// }
