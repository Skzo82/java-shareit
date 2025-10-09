package ru.practicum.shareit.client;

import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Set;

public class BaseClient {

    private static final Set<String> HOP_BY_HOP = Set.of(
            "transfer-encoding", "content-length", "connection",
            "keep-alive", "proxy-connection", "te", "trailer", "upgrade"
    );

    protected final RestTemplate rest;
    private final String serverUrl;

    public BaseClient(RestTemplate restTemplate, String serverUrl) {
        this.rest = restTemplate;
        this.serverUrl = serverUrl;
    }

    protected ResponseEntity<Object> get(String path, Long userId) {
        return forward(exchange(HttpMethod.GET, path, userId, null, null));
    }

    protected ResponseEntity<Object> get(String path, Long userId, Map<String, Object> uriParams) {
        return forward(exchange(HttpMethod.GET, path, userId, null, uriParams));
    }

    protected ResponseEntity<Object> post(String path, Long userId, Object body) {
        return forward(exchange(HttpMethod.POST, path, userId, body, null));
    }

    protected ResponseEntity<Object> patch(String path, Long userId, Object body) {
        return forward(exchange(HttpMethod.PATCH, path, userId, body, null));
    }

    protected ResponseEntity<Object> delete(String path, Long userId) {
        return forward(exchange(HttpMethod.DELETE, path, userId, null, null));
    }

    private ResponseEntity<Object> exchange(
            HttpMethod method, String path, Long userId, Object body, Map<String, Object> uriParams) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (userId != null) {
            headers.add("X-Sharer-User-Id", String.valueOf(userId));
        }

        HttpEntity<Object> entity = (body == null) ? new HttpEntity<>(headers) : new HttpEntity<>(body, headers);
        String url = serverUrl + path;

        try {
            if (uriParams == null || uriParams.isEmpty()) {
                return rest.exchange(url, method, entity, Object.class);
            } else {
                return rest.exchange(url, method, entity, Object.class, uriParams);
            }
        } catch (HttpStatusCodeException ex) {
            HttpHeaders respHeaders = ex.getResponseHeaders() != null ? ex.getResponseHeaders() : new HttpHeaders();
            if (!respHeaders.containsKey(HttpHeaders.CONTENT_TYPE)) {
                respHeaders.setContentType(MediaType.APPLICATION_JSON);
            }
            return new ResponseEntity<>(ex.getResponseBodyAsString(), respHeaders, ex.getStatusCode());
        }
    }

    protected ResponseEntity<Object> forward(ResponseEntity<?> down) {
        HttpHeaders filtered = new HttpHeaders();
        down.getHeaders().forEach((k, v) -> {
            if (!HOP_BY_HOP.contains(k.toLowerCase())) {
                filtered.put(k, v);
            }
        });
        if (!filtered.containsKey(HttpHeaders.CONTENT_TYPE)) {
            filtered.setContentType(MediaType.APPLICATION_JSON);
        }
        return ResponseEntity.status(down.getStatusCode())
                .headers(filtered)
                .body(down.getBody());
    }
}