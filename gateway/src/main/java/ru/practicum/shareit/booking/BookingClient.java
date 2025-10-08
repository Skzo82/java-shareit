package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class BookingClient {

    private static final String USER_HEADER = "X-Sharer-User-Id";
    private static final List<String> HOP_BY_HOP = List.of(
            "connection", "keep-alive", "proxy-authenticate", "proxy-authorization",
            "te", "trailer", "transfer-encoding", "upgrade", "content-length"
    );
    private final RestTemplate restTemplate;
    @Value("${shareit.server.url:http://shareit-server:9090}")
    private String serverUrl;

    public ResponseEntity<Object> create(Long userId, BookItemRequestDto dto) {
        return forward(HttpMethod.POST, "/bookings", userId, dto, null);
    }

    public ResponseEntity<Object> approve(Long userId, Long bookingId, boolean approved) {
        String path = "/bookings/" + bookingId + "?approved=" + approved;
        return forward(HttpMethod.PATCH, path, userId, null, null);
    }

    public ResponseEntity<Object> getById(Long userId, Long bookingId) {
        String path = "/bookings/" + bookingId;
        return forward(HttpMethod.GET, path, userId, null, null);
    }

    public ResponseEntity<Object> getAllByUser(Long userId, String state, int from, int size) {
        String path = "/bookings?state=" + state + "&from=" + from + "&size=" + size;
        return forward(HttpMethod.GET, path, userId, null, null);
    }

    public ResponseEntity<Object> getAllByOwner(Long userId, String state, int from, int size) {
        String path = "/bookings/owner?state=" + state + "&from=" + from + "&size=" + size;
        return forward(HttpMethod.GET, path, userId, null, null);
    }

    private ResponseEntity<Object> forward(HttpMethod method,
                                           String pathWithQuery,
                                           Long userId,
                                           Object body,
                                           Map<String, String> extraHeaders) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set(USER_HEADER, String.valueOf(userId));
        if (extraHeaders != null) extraHeaders.forEach(headers::set);
        HttpEntity<?> entity = (body == null) ? new HttpEntity<>(headers) : new HttpEntity<>(body, headers);

        ResponseEntity<byte[]> down = restTemplate.exchange(
                URI.create(serverUrl + pathWithQuery),
                method,
                entity,
                byte[].class
        );

        HttpHeaders outHeaders = new HttpHeaders(new LinkedMultiValueMap<>());
        down.getHeaders().forEach((k, v) -> {
            String key = k == null ? "" : k.toLowerCase();
            if (!HOP_BY_HOP.contains(key)) {
                outHeaders.put(k, v);
            }
        });

        MediaType mt = down.getHeaders().getContentType();
        if (mt == null) {
            outHeaders.setContentType(MediaType.APPLICATION_JSON);
        }

        return new ResponseEntity<>(decodeBody(down), outHeaders, down.getStatusCode());
    }

    private Object decodeBody(ResponseEntity<byte[]> down) {
        byte[] body = down.getBody();
        if (body == null || body.length == 0) return null;
        return new String(body);
    }
}