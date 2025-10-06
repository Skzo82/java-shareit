package ru.practicum.shareit.request;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Component
public class ItemRequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests"; // Базовый путь на сервере

    public ItemRequestClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> createRequest(Long userId, ItemRequestCreateDto dto) {
        return post(API_PREFIX, userId, dto); // POST /requests
    }

    public ResponseEntity<Object> getOwnRequests(Long userId) {
        return get(API_PREFIX, userId); // GET /requests
    }

    public ResponseEntity<Object> getAllRequests(Long userId, Integer from, Integer size) {
        return get(API_PREFIX + "/all", userId, Map.of("from", from, "size", size)); // GET /requests/all?from..&size..
    }

    public ResponseEntity<Object> getRequestById(Long userId, Long requestId) {
        return get(API_PREFIX + "/" + requestId, userId); // GET /requests/{id}
    }
}
