package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;
import ru.practicum.shareit.client.BaseClient;

import java.io.IOException;
import java.time.Duration;

@Component
public class UserClient extends BaseClient {

    private static final String API_PREFIX = "/users";

    public UserClient(@Value("${shareit.server.url}") String serverUrl,
                      RestTemplateBuilder builder) {
        super(
                builder
                        .rootUri(serverUrl)
                        .setConnectTimeout(Duration.ofSeconds(5))
                        .setReadTimeout(Duration.ofSeconds(30))
                        .errorHandler(new DefaultResponseErrorHandler() {
                            @Override
                            public boolean hasError(ClientHttpResponse response) throws IOException {
                                return false; // non sollevare eccezioni per 4xx/5xx
                            }
                        })
                        .build(),
                serverUrl
        );
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
