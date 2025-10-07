package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

/**
 * Клиент для обращения к серверному сервису бронирований
 */
@Slf4j
@Component
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    public BookingClient(@Value("${shareit-server.url}") String serverUrl,
                         RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                // важно: не вызывать перегруженную requestFactory, чтобы не получить ambiguity
                .build());
    }

    /**
     * Создать бронирование
     */
    public ResponseEntity<Object> create(Long userId, BookItemRequestDto dto) {
        return post("", userId, dto);
    }

    /**
     * Одобрить/отклонить бронирование
     */
    public ResponseEntity<Object> approve(Long userId, Long bookingId, boolean approved) {
        // BaseClient.patch(String path, Long userId, Object body)
        // Формируем queryString вручную, чтобы уложиться в сигнатуру BaseClient
        String path = "/" + bookingId + "?approved=" + approved;
        return patch(path, userId, null);
    }

    /**
     * Получить по id
     */
    public ResponseEntity<Object> getById(Long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    /**
     * Список пользователя
     */
    public ResponseEntity<Object> getAllByUser(Long userId, String state, int from, int size) {
        String path = "?state={state}&from={from}&size={size}";
        Map<String, Object> params = Map.of("state", state, "from", from, "size", size);
        return get(path, userId, params);
    }

    /**
     * Список владельца
     */
    public ResponseEntity<Object> getAllByOwner(Long userId, String state, int from, int size) {
        String path = "/owner?state={state}&from={from}&size={size}";
        Map<String, Object> params = Map.of("state", state, "from", from, "size", size);
        return get(path, userId, params);
    }
}