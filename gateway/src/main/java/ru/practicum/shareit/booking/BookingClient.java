package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.client.BaseClient;

import java.io.IOException;
import java.time.Duration;

@Slf4j
@Component
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit.server.url}") String serverUrl,
                         RestTemplateBuilder builder) {
        super(
                builder
                        .errorHandler(new DefaultResponseErrorHandler() {
                            @Override
                            public boolean hasError(ClientHttpResponse response) throws IOException {
                                return false;
                            }
                        })
                        .requestFactory(settings -> new HttpComponentsClientHttpRequestFactory())
                        .setConnectTimeout(Duration.ofSeconds(5))
                        .setReadTimeout(Duration.ofSeconds(30))
                        .build(),
                serverUrl
        );
    }

    public BookingClient(RestTemplate restTemplate) {
        super(restTemplate, "http://localhost:9090");
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
