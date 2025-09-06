package ru.practicum.shareit.exception;

// Конфликт данных (например, дублирующийся email)
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}