package com.aliaa.accountservice.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LogEntry {
    private String message;
    private String messageType;
    private LocalDateTime dateTime;
}