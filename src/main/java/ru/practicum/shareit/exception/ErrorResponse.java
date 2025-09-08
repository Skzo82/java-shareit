package ru.practicum.shareit.exception;

import java.time.Instant;

// Простая DTO для ошибок API
public class ErrorResponse {
    private final String message;
    private final Instant timestamp = Instant.now();

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() { return message; }
    public Instant getTimestamp() { return timestamp; }
}
