package com.aliaa.accountservice.logging;

import com.aliaa.accountservice.dto.LogEntry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class LoggingProducer {

    @Autowired
    private KafkaTemplate<String, LogEntry> kafkaTemplate;

    private final ObjectMapper mapper;

    public LoggingProducer() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public void sendLog(Object message, String messageType) {

        try {
            LogEntry log = new LogEntry();
            log.setMessage(mapper.writeValueAsString(message));
            log.setMessageType(messageType);
            log.setDateTime(java.time.LocalDateTime.now());
            kafkaTemplate.send("logs", log);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}