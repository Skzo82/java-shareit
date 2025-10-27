package ru.practicum.shareit.error;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.NOT_FOUND;

class ApiErrorHandlerTest {

    private final ApiErrorHandler handler = new ApiErrorHandler();

    @Test
    void handleHttpStatusCode_propagatesStatusAndBody() {
        RestClientResponseException ex = HttpClientErrorException.create(
                NOT_FOUND, "Not Found",
                new HttpHeaders(),
                "{\"error\":\"not found\"}".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
        );

        ResponseEntity<Object> resp = handler.handleHttpStatusCode(ex);

        assertThat(resp.getStatusCode()).isEqualTo(NOT_FOUND);
        assertThat(String.valueOf(resp.getBody())).contains("not found");
        assertThat(resp.getHeaders().getContentType()).isNotNull();
    }

    @Test
    void handleRestClient_returns500Fallback() {
        RestClientException ex = new RestClientException("boom");

        ResponseEntity<Object> resp = handler.handleRestClient(ex);

        assertThat(resp.getStatusCode().value()).isEqualTo(500);
        assertThat(String.valueOf(resp.getBody())).contains("boom");
        assertThat(resp.getHeaders().getContentType()).isNotNull();
    }
}