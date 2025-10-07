package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.client.BaseClient;

@Component
public class ItemRequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl,
                             RestTemplateBuilder builder) {
        super(builder.build(), serverUrl);
    }

    public ResponseEntity<Object> createRequest(Long userId, ItemRequestCreateDto dto) {
        return post(API_PREFIX, userId, dto);
    }

    public ResponseEntity<Object> getOwnRequests(Long userId) {
        return get(API_PREFIX, userId);
    }

    public ResponseEntity<Object> getAllRequests(Long userId, int from, int size) {
        String path = API_PREFIX + "/all?from=" + from + "&size=" + size;
        return get(path, userId);
    }

    public ResponseEntity<Object> getRequestById(Long userId, Long requestId) {
        return get(API_PREFIX + "/" + requestId, userId);
    }
}