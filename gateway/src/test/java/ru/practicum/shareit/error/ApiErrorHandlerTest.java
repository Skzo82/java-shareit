package ru.practicum.shareit.error;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ApiErrorHandlerTest {

    private final ApiErrorHandler handler = new ApiErrorHandler();

    @Test
    @DisplayName("handleHttpStatusCode: propaga status, headers e body originali (es. 404 downstream)")
    void handleHttpStatusCode_propagatesStatusHeadersAndBody() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        var ex = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND,
                "Not Found",
                headers,
                "{\"error\":\"nope\"}".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
        );

        ResponseEntity<String> resp = handler.handleHttpStatusCode(ex);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resp.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(resp.getBody()).isEqualTo("{\"error\":\"nope\"}");
    }

    @Test
    @DisplayName("handleHttpStatusCode: gestisce anche 500 downstream")
    void handleHttpStatusCode_handlesServerError() {
        var ex = HttpServerErrorException.create(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Server Error",
                new HttpHeaders(),
                "{\"error\":\"boom\"}".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
        );

        ResponseEntity<String> resp = handler.handleHttpStatusCode(ex);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(resp.getBody()).isEqualTo("{\"error\":\"boom\"}");
    }

    @Test
    @DisplayName("handleBadRequest: ritorna 400 con messaggio personalizzato")
    void handleBadRequest_mapsTo400() {
        var resp = handler.handleBadRequest(new IllegalArgumentException("wrong arg"));
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp.getBody()).isEqualTo(Map.of("error", "wrong arg"));
    }

    @Test
    @DisplayName("handleOther: ritorna 500 Internal Server Error")
    void handleOther_mapsTo500() {
        var resp = handler.handleOther(new RuntimeException("unexpected"));
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(resp.getBody()).isEqualTo(Map.of("error", "Internal server error"));
    }
}