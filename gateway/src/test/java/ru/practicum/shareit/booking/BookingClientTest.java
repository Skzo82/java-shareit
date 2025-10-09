package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class BookingClientTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer server;
    private BookingClient client;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        server = MockRestServiceServer.bindTo(restTemplate).build();
        client = new BookingClient(restTemplate);
        ReflectionTestUtils.setField(client, "serverUrl", "http://localhost:9090");
    }

    @Test
    void create_decodesUtf8_andPassesUserHeader() {
        var dto = new BookItemRequestDto();

        server.expect(once(), requestTo(URI.create("http://localhost:9090/bookings")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess("{\"ok\":true}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> resp = client.create(1L, dto);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isEqualTo("{\"ok\":true}");
        server.verify();
    }

    @Test
    void getById_filtersHopByHopHeaders_andSetsDefaultContentType() {
        HttpHeaders h = new HttpHeaders();
        h.add("Connection", "keep-alive"); // header hop-by-hop, deve sparire nella risposta del gateway

        server.expect(once(), requestTo("http://localhost:9090/bookings/5"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK).headers(h).body("{}"));

        ResponseEntity<Object> resp = client.getById(42L, 5L);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getHeaders().containsKey("Connection")).isFalse();
        assertThat(resp.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(resp.getBody()).isEqualTo("{}");
        server.verify();
    }
}