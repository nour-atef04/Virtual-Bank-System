package com.example.logging_service.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LogEntryDto {
    private String message;
    private String messageType;
    private LocalDateTime dateTime;
}
