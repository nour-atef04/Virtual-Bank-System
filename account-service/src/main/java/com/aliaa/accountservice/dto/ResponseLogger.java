package com.aliaa.accountservice.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ResponseLogger {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void logResponse(Object response) {
        try {
            String json = objectMapper.writeValueAsString(response);
            log.info("RESPONSE BODY: {}", json);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize response", e);
        }
    }
}