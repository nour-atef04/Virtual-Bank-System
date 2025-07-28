package com.example.user_service.logging;

import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.example.user_service.dto.UserLogin;
import com.example.user_service.dto.UserRegistration;
import com.example.user_service.exception.*;
import com.example.user_service.mapper.LogMapper;

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

    @Before("execution(* com.example.user_service.controller..*.*(..))")
    public void logBeforeRequest(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            Object originalRequest = args[0];
            Object safeLoggable;

            if (originalRequest instanceof UserLogin) {
                safeLoggable = LogMapper.toLoggable((UserLogin) originalRequest);
            } else if (originalRequest instanceof UserRegistration) {
                safeLoggable = LogMapper.toLoggable((UserRegistration) originalRequest);
            } else {
                safeLoggable = originalRequest;
            }

            loggingProducer.sendLog(safeLoggable, "REQUEST");
        }
    }

    @AfterReturning(pointcut = "execution(* com.example.user_service.controller..*.*(..))", returning = "result")
    public void logAfterResponse(JoinPoint joinPoint, Object result) {
        Object responseBody = result;

        // If it's a ResponseEntity, extract the body
        if (result instanceof ResponseEntity) {
            ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;
            responseBody = responseEntity.getBody();
        }

        loggingProducer.sendLog(responseBody, "RESPONSE");
    }

    @AfterThrowing(pointcut = "execution(* com.example.user_service..*.*(..))", throwing = "ex")
    public void logAfterException(JoinPoint joinPoint, Throwable ex) {
        Map<String, Object> errorLog = new LinkedHashMap<>();

        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);
        if (responseStatus != null) {
            errorLog.put("status", responseStatus.code().value());
            errorLog.put("error", responseStatus.reason().isEmpty()
                    ? responseStatus.code().getReasonPhrase()
                    : responseStatus.reason());
        } else {
            errorLog.put("status", 500);
            errorLog.put("error", "Internal Server Error");
        }

        errorLog.put("message", ex.getMessage());
        loggingProducer.sendLog(errorLog, "ERROR");
    }

}
