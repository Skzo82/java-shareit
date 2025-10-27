package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

class BaseClientTest {

    private RestTemplate rest;
    private MockRestServiceServer server;
    private TestClient client;

    @BeforeEach
    void setUp() {
        rest = new RestTemplate();
        server = MockRestServiceServer.bindTo(rest).build();
        client = new TestClient(rest, "http://localhost:9090");
    }

    @Test
    void exchange_withUriParams_andUserHeader_ok() {
        server.expect(once(), requestTo("http://localhost:9090/items?from=0&size=5"))
                .andExpect(header("X-Sharer-User-Id", "7"))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));

        ResponseEntity<Object> resp =
                client.getWithParams(7L, "/items?from={from}&size={size}", Map.of("from", 0, "size", 5));

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(String.valueOf(resp.getBody())).isEqualTo("[]");
        server.verify();
    }

    @Test
    void exchange_propagatesDownstreamHttpError_asIs() {
        server.expect(once(), requestTo("http://localhost:9090/not-found"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\":\"Not found\"}".getBytes(StandardCharsets.UTF_8)));

        ResponseEntity<Object> resp = client.get("/not-found", 1L);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resp.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(resp.getBody()).isEqualTo("{\"error\":\"Not found\"}");
        server.verify();
    }

    @Test
    void forward_stripsHopByHop_andSetsDefaultContentType() {
        ResponseEntity<?> downstream = ResponseEntity.status(200)
                .header("Connection", "keep-alive") // hop-by-hop: va rimosso
                .body("{}");

        ResponseEntity<Object> resp = client.callForward(downstream);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getHeaders().containsKey("Connection")).isFalse();
        assertThat(resp.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(resp.getBody()).isEqualTo("{}");
    }

    static class TestClient extends BaseClient {
        TestClient(RestTemplate r, String url) {
            super(r, url);
        }

        ResponseEntity<Object> getWithParams(Long userId, String path, Map<String, Object> p) {
            return super.get(path, userId, p);
        }

        ResponseEntity<Object> callForward(ResponseEntity<?> down) {
            return super.forward(down);
        }
    }
}