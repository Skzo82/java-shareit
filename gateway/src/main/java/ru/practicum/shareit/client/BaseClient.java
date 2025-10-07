package ru.practicum.shareit.client;

import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

public class BaseClient {

    private static final String USER_HEADER = "X-Sharer-User-Id"; // Имя заголовка пользователя
    private final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    // --- GET ---
    protected ResponseEntity<Object> get(String path, Long userId) {
        return get(path, userId, null);
    }

    protected ResponseEntity<Object> get(String path, Long userId, @Nullable Map<String, Object> params) {
        return exchange(HttpMethod.GET, path, userId, null, params);
    }

    // --- POST ---
    protected ResponseEntity<Object> post(String path, Long userId, @Nullable Object body) {
        return exchange(HttpMethod.POST, path, userId, body, null);
    }

    // --- PATCH ---
    protected ResponseEntity<Object> patch(String path, Long userId, @Nullable Object body) {
        return exchange(HttpMethod.PATCH, path, userId, body, null);
    }

    // --- DELETE ---
    protected ResponseEntity<Object> delete(String path, Long userId) {
        return exchange(HttpMethod.DELETE, path, userId, null, null);
    }

    // Общий метод обмена запросами с сервером
    private ResponseEntity<Object> exchange(HttpMethod method,
                                            String path,
                                            Long userId,
                                            @Nullable Object body,
                                            @Nullable Map<String, Object> params) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (userId != null) {
            headers.add(USER_HEADER, String.valueOf(userId)); // Прокидываем ID пользователя
        }

        HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers);

        String uri = path;
        if (params != null && !params.isEmpty()) {
            UriComponentsBuilder builder = UriComponentsBuilder.fromPath(path);
            params.forEach((k, v) -> builder.queryParam(k, v));
            uri = builder.toUriString();
        }

        try {
            return rest.exchange(uri, method, requestEntity, Object.class);
        } catch (HttpStatusCodeException e) {
            // Возвращаем статус/тело сервера, чтобы не терять детали ошибки
            return ResponseEntity.status(e.getStatusCode())
                    .headers(e.getResponseHeaders() != null ? e.getResponseHeaders() : new HttpHeaders())
                    .body(e.getResponseBodyAsByteArray());
        }
    }
}
