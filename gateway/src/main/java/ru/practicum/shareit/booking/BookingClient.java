package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.client.BaseClient;

@Component
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    public BookingClient(@Value("${shareit-server.url}") String serverUrl,
                         RestTemplateBuilder builder) {
        super(builder.build(), serverUrl);
    }

    // CREATE
    public ResponseEntity<Object> create(Long userId, Object dto) {
        return post(API_PREFIX, userId, dto);
    }

    // APPROVE / REJECT
    public ResponseEntity<Object> approve(Long userId, Long bookingId, boolean approved) {
        String path = API_PREFIX + "/" + bookingId + "?approved=" + approved;
        return patch(path, userId, null);
    }

    // GET BY ID
    public ResponseEntity<Object> getById(Long userId, Long bookingId) {
        return get(API_PREFIX + "/" + bookingId, userId);
    }

    public ResponseEntity<Object> getAllByUser(Long userId, String state, int from, int size) {
        String path = API_PREFIX + "?state=" + state + "&from=" + from + "&size=" + size;
        return get(path, userId);
    }

    public ResponseEntity<Object> getAllByOwner(Long userId, String state, int from, int size) {
        String path = API_PREFIX + "/owner?state=" + state + "&from=" + from + "&size=" + size;
        return get(path, userId);
    }

    public ResponseEntity<Object> getAllByBooker(Long userId, String state, int from, int size) {
        return getAllByUser(userId, state, from, size);
    }
}