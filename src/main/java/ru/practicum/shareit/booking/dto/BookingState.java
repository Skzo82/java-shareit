package ru.practicum.shareit.booking.dto;

public enum BookingState {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED;

    public static BookingState from(String s) {
        try {
            return s == null ? ALL : BookingState.valueOf(s.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Unknown state: " + s);
        }
    }
}
