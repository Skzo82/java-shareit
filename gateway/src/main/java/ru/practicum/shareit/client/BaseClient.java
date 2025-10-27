package ru.practicum.shareit.client;

import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BaseClient {

    private static final Set<String> HOP_BY_HOP = Set.of(
            "transfer-encoding", "content-length", "connection",
            "keep-alive", "proxy-connection", "te", "trailer", "upgrade"
    );

    private static final MediaType JSON = MediaType.APPLICATION_JSON;

    protected final RestTemplate rest;
    private final String serverUrl;

    public BaseClient(RestTemplate restTemplate, String serverUrl) {
        this.rest = restTemplate;
        this.serverUrl = serverUrl;
    }

    /* ========== Public helpers ========== */

    private static HttpHeaders safeCopy(HttpHeaders original) {
        HttpHeaders copy = new HttpHeaders();
        if (original != null) {
            original.forEach(copy::put);
        }
        return copy;
    }

    protected ResponseEntity<Object> get(String path, Long userId) {
        return doExchange(HttpMethod.GET, path, userId, null, null);
    }

    protected ResponseEntity<Object> get(String path, Long userId, Map<String, Object> uriParams) {
        return doExchange(HttpMethod.GET, path, userId, null, uriParams);
    }

    protected ResponseEntity<Object> post(String path, Long userId, Object body) {
        return doExchange(HttpMethod.POST, path, userId, body, null);
    }

    protected ResponseEntity<Object> post(String path, Long userId, Object body, Map<String, Object> uriParams) {
        return doExchange(HttpMethod.POST, path, userId, body, uriParams);
    }

    protected ResponseEntity<Object> patch(String path, Long userId, Object body) {
        return doExchange(HttpMethod.PATCH, path, userId, body, null);
    }

    /* ========== Core ========== */

    protected ResponseEntity<Object> delete(String path, Long userId) {
        return doExchange(HttpMethod.DELETE, path, userId, null, null);
    }

    private ResponseEntity<Object> doExchange(
            HttpMethod method, String path, Long userId, Object body, Map<String, Object> uriParams
    ) {
        String url = serverUrl + path;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(JSON);
        if (userId != null) {
            headers.add("X-Sharer-User-Id", String.valueOf(userId));
        }

        HttpEntity<Object> requestEntity = (body == null)
                ? new HttpEntity<>(headers)
                : new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Object> resp = (uriParams == null || uriParams.isEmpty())
                    ? rest.exchange(url, method, requestEntity, Object.class)
                    : rest.exchange(url, method, requestEntity, Object.class, uriParams);

            return forward(resp);

        } catch (HttpStatusCodeException ex) {
            HttpHeaders respHeaders = safeCopy(ex.getResponseHeaders());
            if (!respHeaders.containsKey(HttpHeaders.CONTENT_TYPE)) {
                respHeaders.setContentType(JSON);
            }
            return ResponseEntity.status(ex.getStatusCode())
                    .headers(respHeaders)
                    .body(ex.getResponseBodyAsString());

        } catch (RestClientException ex) {
            Map<String, String> payload = new HashMap<>();
            payload.put("error", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(JSON)
                    .body(payload);
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
            filtered.setContentType(JSON);
        }
        return ResponseEntity.status(down.getStatusCode())
                .headers(filtered)
                .body(down.getBody());
    }
}