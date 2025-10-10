package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Component
public class BookingClient extends BaseClient {

    @Autowired
    public BookingClient(RestTemplate restTemplate,
                         @Value("${shareit.server.url}") String serverUrl) {
        super(restTemplate, serverUrl);
    }

    public BookingClient(RestTemplate restTemplate) {
        super(restTemplate, "http://localhost:9090");
    }

    public ResponseEntity<Object> create(Long userId, BookingRequestDto dto) {
        return post("/bookings", userId, dto);
    }

    public ResponseEntity<Object> approve(Long ownerId, Long bookingId, boolean approved) {
        String path = String.format("/bookings/%d?approved=%s", bookingId, approved);
        return patch(path, ownerId, null);
    }

    public ResponseEntity<Object> getById(Long userId, Long bookingId) {
        return get("/bookings/{id}", userId, Map.of("id", bookingId));
    }

    public ResponseEntity<Object> getAllByUser(Long userId, String state, int from, int size) {
        return get("/bookings?state={state}&from={from}&size={size}", userId,
                Map.of("state", state, "from", from, "size", size));
    }

    public ResponseEntity<Object> getAllByOwner(Long ownerId, String state, int from, int size) {
        return get("/bookings/owner?state={state}&from={from}&size={size}", ownerId,
                Map.of("state", state, "from", from, "size", size));
    }
}
