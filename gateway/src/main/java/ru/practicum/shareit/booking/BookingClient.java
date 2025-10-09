package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.client.BaseClient;

@Component
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    public BookingClient(@Value("${shareit.server.url}") String serverUrl,
                         RestTemplate restTemplate) {
        super(restTemplate, serverUrl);
    }

    public ResponseEntity<Object> create(Long userId, BookingRequestDto dto) {
        return post(API_PREFIX, userId, dto);
    }

    public ResponseEntity<Object> approve(Long userId, Long bookingId, boolean approved) {
        return patch(API_PREFIX + "/" + bookingId + "?approved=" + approved, userId, null);
    }

    public ResponseEntity<Object> getById(Long userId, Long bookingId) {
        return get(API_PREFIX + "/" + bookingId, userId);
    }

    public ResponseEntity<Object> getAllByUser(Long userId, String state, int from, int size) {
        return get(API_PREFIX + "?state=" + state + "&from=" + from + "&size=" + size, userId);
    }

    public ResponseEntity<Object> getAllByOwner(Long userId, String state, int from, int size) {
        return get(API_PREFIX + "/owner?state=" + state + "&from=" + from + "&size=" + size, userId);
    }
}
