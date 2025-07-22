package com.aliaa.accountservice.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private final LoggingProducer loggingProducer;

    public LoggingAspect(LoggingProducer loggingProducer) {
        this.loggingProducer = loggingProducer;
    }

    @Before("execution(* com.aliaa.accountservice.controller.*.*(..))")
    public void logBeforeRequest(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            loggingProducer.sendLog(args[0], "REQUEST_" + joinPoint.getSignature().getName());
        }
    }

    @AfterReturning(pointcut = "execution(* com.aliaa.accountservice.controller.*.*(..))", returning = "result")
    public void logAfterResponse(JoinPoint joinPoint, Object result) {
        if (result != null) {
            loggingProducer.sendLog(result, "RESPONSE_" + joinPoint.getSignature().getName());
        }
    }
}