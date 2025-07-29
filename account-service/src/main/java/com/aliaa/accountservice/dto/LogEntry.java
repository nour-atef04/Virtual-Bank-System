package com.aliaa.accountservice.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogEntry {
    private String message;
    private String messageType;
    private LocalDateTime dateTime;
}