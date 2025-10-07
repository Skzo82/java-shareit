package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ItemClientTest {

    private ItemClient client;
    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        // “iniettiamo” il RestTemplate in BaseClient tramite sottoclasse di test
        client = new ItemClient("http://localhost:9090", new RestTemplateBuilder()) {
            // esponi il RestTemplate interno se necessario, oppure crea un costruttore ad hoc
        };
        // Hack semplice: riflessione per sostituire il RestTemplate privato in BaseClient
        try {
            var f = ru.practicum.shareit.client.BaseClient.class.getDeclaredField("rest");
            f.setAccessible(true);
            f.set(client, restTemplate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void create_ok() {
        server.expect(ExpectedCount.once(), requestTo("http://localhost:9090/items"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess("{\"id\":10}", MediaType.APPLICATION_JSON));

        ItemCreateDto dto = new ItemCreateDto();
        dto.setName("N");
        dto.setDescription("D");
        dto.setAvailable(true);

        ResponseEntity<Object> resp = client.create(1L, dto);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        server.verify();
    }

    @Test
    void update_ok() {
        server.expect(ExpectedCount.once(), requestTo("http://localhost:9090/items/12"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header("X-Sharer-User-Id", "2"))
                .andRespond(withSuccess("{\"id\":12}", MediaType.APPLICATION_JSON));

        ItemUpdateDto dto = new ItemUpdateDto();
        dto.setName("X");

        ResponseEntity<Object> resp = client.update(2L, 12L, dto);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        server.verify();
    }

    @Test
    void search_ok() {
        server.expect(requestTo("http://localhost:9090/items/search?text=drill&from=0&size=10"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "5"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        client.search(5L, "drill", 0, 10);
        server.verify();
    }

    @Test
    void getOwnerItems_ok() {
        server.expect(requestTo("http://localhost:9090/items?from=0&size=5"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "7"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        client.getOwnerItems(7L, 0, 5);
        server.verify();
    }

    @Test
    void getById_ok() {
        server.expect(requestTo("http://localhost:9090/items/99"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "3"))
                .andRespond(withSuccess("{\"id\":99}", MediaType.APPLICATION_JSON));

        client.getById(3L, 99L);
        server.verify();
    }
}