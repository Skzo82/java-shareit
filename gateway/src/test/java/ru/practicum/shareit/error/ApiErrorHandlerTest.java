package ru.practicum.shareit.error;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ApiErrorHandlerTest {

    private final ApiErrorHandler handler = new ApiErrorHandler();

    @Test
    void handleDownstream_propagatesStatusHeadersAndBody() {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        var ex = HttpClientErrorException.NotFound.create(
                HttpStatus.NOT_FOUND, "Not Found", h,
                "{\"error\":\"nope\"}".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);

        ResponseEntity<String> resp = handler.handleDownstream(ex);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resp.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(resp.getBody()).isEqualTo("{\"error\":\"nope\"}");
    }

    @Test
    void handleBadRequest_mapsTo400() {
        var resp = handler.handleBadRequest(new IllegalArgumentException("bad"));
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp.getBody()).isEqualTo(Map.of("error", "bad"));
    }

    @Test
    void handleOther_mapsTo500() {
        var resp = handler.handleOther(new RuntimeException("x"));
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(resp.getBody()).isEqualTo(Map.of("error", "Internal server error"));
    }
}
