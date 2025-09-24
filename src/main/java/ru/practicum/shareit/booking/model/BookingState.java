package ru.practicum.shareit.booking.model;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED,
    CANCELED;

    public static BookingState from(String s) {
        try {
            return s == null ? ALL : BookingState.valueOf(s.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Unknown state: " + s);
        }
    }
}