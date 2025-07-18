package com.example.logging_service.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.logging_service.model.LogEntry;
import com.example.logging_service.repository.LogEntryRepository;
import com.example.logging_service.dto.LogEntryDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Service
public class LoggingConsumer {

    @Autowired
    private LogEntryRepository logRepo;

    private final ObjectMapper mapper;

    public LoggingConsumer() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @KafkaListener(topics = "logs", groupId = "logging-group")
    public void consume(String messageJson) {
        System.out.println("Received message: " + messageJson);
        try {
            LogEntryDto logDto = mapper.readValue(messageJson, LogEntryDto.class);

            System.out.println("Deserializing into: " + LogEntryDto.class.getName());

            LogEntry log = new LogEntry();
            log.setMessage(logDto.getMessage());
            log.setMessageType(logDto.getMessageType());
            log.setDateTime(logDto.getDateTime());

            logRepo.save(log);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
