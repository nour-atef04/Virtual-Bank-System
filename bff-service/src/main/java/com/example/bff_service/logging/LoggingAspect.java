package com.example.bff_service.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Aspect
@Component
@Slf4j
public class LoggingAspect {
    private final LoggingProducer loggingProducer;

    public LoggingAspect(LoggingProducer loggingProducer) {
        this.loggingProducer = loggingProducer;
    }

    @Around("execution(* com.example.bff_service.controller.*.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof String) {
            String userId = (String) args[0];
            loggingProducer.sendLog(userId, "REQUEST");
        }

        Object result = joinPoint.proceed();

        if (result instanceof Mono) {
            return ((Mono<?>) result)
                    .doOnSuccess(response -> {
                        if (response instanceof ResponseEntity) {
                            Object body = ((ResponseEntity<?>) response).getBody();
                            loggingProducer.sendLog(body, "RESPONSE");
                        }
                    })
                    .doOnError(e -> log.error("Error processing request", e));
        }

        return result;
    }
}