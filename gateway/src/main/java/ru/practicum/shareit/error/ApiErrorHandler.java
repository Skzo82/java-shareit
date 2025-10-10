package ru.practicum.shareit.error;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.Set;

@RestControllerAdvice
public class ApiErrorHandler {

    private static final Set<String> HOP_BY_HOP = Set.of(
            "transfer-encoding", "content-length", "connection",
            "keep-alive", "proxy-connection", "te", "trailer", "upgrade"
    );

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<Object> handleHttpStatusCode(RestClientResponseException ex) {
        HttpHeaders headers = new HttpHeaders();

        HttpHeaders src = ex.getResponseHeaders();
        if (src != null) {
            src.forEach((k, v) -> {
                if (k != null && !HOP_BY_HOP.contains(k.toLowerCase())) {
                    headers.put(k, v);
                }
            });
        }
        if (!headers.containsKey(HttpHeaders.CONTENT_TYPE)) {
            headers.setContentType(MediaType.APPLICATION_JSON);
        }

        Object body;
        try {
            byte[] bytes = ex.getResponseBodyAsByteArray();
            body = (bytes != null && bytes.length > 0) ? ex.getResponseBodyAsString() : ex.getMessage();
        } catch (Exception ignored) {
            body = ex.getMessage();
        }

        return new ResponseEntity<>(body, headers, ex.getStatusCode());
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<Object> handleRestClient(RestClientException ex) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String safe = (ex.getMessage() == null ? "Client error" : ex.getMessage()).replace("\"", "\\\"");
        String json = "{\"error\":\"" + safe + "\"}";
        return new ResponseEntity<>(json, headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}