package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.client.BaseClient;

@Component
public class UserClient extends BaseClient {

    private static final String API_PREFIX = "/users";

    public UserClient(@Value("${shareit.server.url}") String serverUrl,
                      RestTemplateBuilder builder) {
        super(builder.build(), serverUrl);
    }

    // --- CREATE ---
    public ResponseEntity<Object> create(Object dto) {
        // Per gli endpoint user di solito non serve l'header X-Sharer-User-Id
        return post(API_PREFIX, null, dto);
    }

    // --- READ ALL ---
    public ResponseEntity<Object> getAll() {
        return get(API_PREFIX, null);
    }

    // --- READ ALL ---
    public ResponseEntity<Object> getAll(int from, int size) {
        String path = API_PREFIX + "?from=" + from + "&size=" + size;
        return get(path, null);
    }

    // --- READ BY ID ---
    public ResponseEntity<Object> getById(Long userId) {
        return get(API_PREFIX + "/" + userId, null);
    }

    // --- UPDATE ---
    public ResponseEntity<Object> update(Long userId, Object dto) {
        return patch(API_PREFIX + "/" + userId, null, dto);
    }

    // --- DELETE ---
    public ResponseEntity<Object> delete(Long userId) {
        return delete(API_PREFIX + "/" + userId, null);
    }
}