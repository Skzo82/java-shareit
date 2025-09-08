package ru.practicum.shareit.exception;

// Исключение для случаев, когда сущность не найдена.
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}