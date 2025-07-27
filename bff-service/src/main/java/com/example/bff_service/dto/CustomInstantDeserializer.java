package com.example.bff_service.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CustomInstantDeserializer extends JsonDeserializer<Instant> {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

    @Override
    public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String dateString = p.getText();
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(dateString, FORMATTER);
            return localDateTime.atZone(ZoneOffset.UTC).toInstant();
        } catch (DateTimeParseException e) {
            try {
                return Instant.parse(dateString);
            } catch (DateTimeParseException e2) {
                throw new IOException("Failed to parse timestamp: " + dateString, e2);
            }
        }
    }
}