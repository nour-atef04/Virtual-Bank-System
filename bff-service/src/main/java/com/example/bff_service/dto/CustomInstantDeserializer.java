package com.example.bff_service.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;

public class CustomInstantDeserializer extends JsonDeserializer<Instant> {

    @Override
    public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken token = p.currentToken();

        if (token == JsonToken.VALUE_NUMBER_FLOAT || token == JsonToken.VALUE_NUMBER_INT) {
            // Handles numeric epoch timestamp (e.g., 1753654873.463610000)
            double epochSeconds = p.getDoubleValue();
            long seconds = (long) epochSeconds;
            long nanos = (long) ((epochSeconds - seconds) * 1_000_000_000);
            return Instant.ofEpochSecond(seconds, nanos);
        } else if (token == JsonToken.VALUE_STRING) {
            String raw = p.getText().trim();
            if (raw.isEmpty()) {
                return null;
            }

            try {
                return Instant.parse(normalizeToIsoWithZ(raw));
            } catch (Exception e) {
                throw new IOException("Failed to parse timestamp: " + raw, e);
            }
        }

        throw new IOException("Unsupported token type for timestamp: " + token);
    }

    private String normalizeToIsoWithZ(String input) {
        if (!input.contains(".")) {
            return input ;
        }

        String[] parts = input.split("\\.");
        String beforeDot = parts[0];
        String fractionAndZone = parts[1];

        StringBuilder fraction = new StringBuilder();
        StringBuilder zone = new StringBuilder();

        for (char ch : fractionAndZone.toCharArray()) {
            if (Character.isDigit(ch)) {
                fraction.append(ch);
            } else {
                zone.append(ch);
            }
        }

        // Normalize fractional seconds to 6 digits
        while (fraction.length() < 6) {
            fraction.append("0");
        }
        if (fraction.length() > 6) {
            fraction.setLength(6);
        }

        String finalZone = zone.length() > 0 ? zone.toString() : "Z";
        return beforeDot + "." + fraction + finalZone;
    }
}
