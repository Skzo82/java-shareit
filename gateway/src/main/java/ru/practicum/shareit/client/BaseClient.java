package ru.practicum.shareit.client;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Set;

public class BaseClient {

    private static final Set<String> HOP_BY_HOP = Set.of(
            "transfer-encoding", "content-length", "connection",
            "keep-alive", "proxy-connection", "te", "trailer", "upgrade"
    );
    private final RestTemplate restTemplate;
    private final String serverUrl;

    public BaseClient(RestTemplate restTemplate, String serverUrl) {
        this.restTemplate = restTemplate;
        this.serverUrl = serverUrl;
    }

    protected ResponseEntity<Object> get(String path, Long userId) {
        ResponseEntity<Object> down = exchange(HttpMethod.GET, path, userId, null, null);
        return forward(down);
    }

    protected ResponseEntity<Object> get(String path, Long userId, Map<String, Object> uriParams) {
        ResponseEntity<Object> down = exchange(HttpMethod.GET, path, userId, null, uriParams);
        return forward(down);
    }

    protected ResponseEntity<Object> post(String path, Long userId, Object body) {
        ResponseEntity<Object> down = exchange(HttpMethod.POST, path, userId, body, null);
        return forward(down);
    }

    protected ResponseEntity<Object> patch(String path, Long userId, Object body) {
        ResponseEntity<Object> down = exchange(HttpMethod.PATCH, path, userId, body, null);
        return forward(down);
    }

    protected ResponseEntity<Object> delete(String path, Long userId) {
        ResponseEntity<Object> down = exchange(HttpMethod.DELETE, path, userId, null, null);
        return forward(down);
    }

    // --- low level ---

    private ResponseEntity<Object> exchange(
            HttpMethod method, String path, Long userId, Object body, Map<String, Object> uriParams) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (userId != null) {
            headers.add("X-Sharer-User-Id", String.valueOf(userId));
        }

        HttpEntity<Object> entity = (body == null) ? new HttpEntity<>(headers) : new HttpEntity<>(body, headers);
        String url = serverUrl + path;

        if (uriParams == null || uriParams.isEmpty()) {
            return restTemplate.exchange(url, method, entity, Object.class);
        } else {
            return restTemplate.exchange(url, method, entity, Object.class, uriParams);
        }
    }

    protected ResponseEntity<Object> forward(ResponseEntity<?> down) {
        HttpHeaders filtered = new HttpHeaders();
        down.getHeaders().forEach((k, v) -> {
            if (!HOP_BY_HOP.contains(k.toLowerCase())) {
                filtered.put(k, v);
            }
        });
        return ResponseEntity.status(down.getStatusCode())
                .headers(filtered)
                .body(down.getBody());
    }
}
