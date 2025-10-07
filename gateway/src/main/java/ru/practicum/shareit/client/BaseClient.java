package ru.practicum.shareit.client;

import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

public class BaseClient {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    private final RestTemplate rest;
    private final String baseUrl;

    public BaseClient(RestTemplate rest, String baseUrl) {
        this.rest = rest;
        this.baseUrl = baseUrl != null && baseUrl.endsWith("/")
                ? baseUrl.substring(0, baseUrl.length() - 1)
                : baseUrl;
    }

    // --- GET ---
    protected ResponseEntity<Object> get(String path, Long userId) {
        return exchange(HttpMethod.GET, path, userId, null);
    }

    // --- POST ---
    protected ResponseEntity<Object> post(String path, Long userId, @Nullable Object body) {
        return exchange(HttpMethod.POST, path, userId, body);
    }

    // --- PATCH ---
    protected ResponseEntity<Object> patch(String path, Long userId, @Nullable Object body) {
        return exchange(HttpMethod.PATCH, path, userId, body);
    }

    // --- DELETE ---
    protected ResponseEntity<Object> delete(String path, Long userId) {
        return exchange(HttpMethod.DELETE, path, userId, null);
    }

    private ResponseEntity<Object> exchange(HttpMethod method,
                                            String path,
                                            Long userId,
                                            @Nullable Object body) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (userId != null) {
            headers.add(USER_HEADER, String.valueOf(userId));
        }

        HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers);

        String normalizedPath = (path == null) ? "" : path.trim();
        if (!normalizedPath.startsWith("/")) {
            normalizedPath = "/" + normalizedPath;
        }
        String absoluteUrl = baseUrl + normalizedPath;

        try {
            return rest.exchange(absoluteUrl, method, requestEntity, Object.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity
                    .status(e.getStatusCode())
                    .headers(e.getResponseHeaders() != null ? e.getResponseHeaders() : new HttpHeaders())
                    .body(e.getResponseBodyAsByteArray());
        }
    }
}