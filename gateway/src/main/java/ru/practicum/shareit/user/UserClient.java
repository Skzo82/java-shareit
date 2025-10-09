package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;

@Component
public class UserClient extends BaseClient {

    private static final String API_PREFIX = "/users";

    public UserClient(@Value("${shareit.server.url}") String serverUrl,
                      RestTemplate restTemplate) {
        super(restTemplate, serverUrl);
    }

    public ResponseEntity<Object> create(Object dto) {
        return post(API_PREFIX, null, dto);
    }

    public ResponseEntity<Object> getAll() {
        return get(API_PREFIX, null);
    }

    public ResponseEntity<Object> getAll(int from, int size) {
        return get(API_PREFIX + "?from=" + from + "&size=" + size, null);
    }

    public ResponseEntity<Object> getById(Long userId) {
        return get(API_PREFIX + "/" + userId, null);
    }

    public ResponseEntity<Object> update(Long userId, Object dto) {
        return patch(API_PREFIX + "/" + userId, null, dto);
    }

    public ResponseEntity<Object> delete(Long userId) {
        return delete(API_PREFIX + "/" + userId, null);
    }
}
