package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Дополнительные тесты для ItemRequestController — проверяем валидацию и отсутствие заголовка.
 */
@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerMoreTest {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestClient client;

    @Test
    @DisplayName("POST /requests — 400, если отсутствует заголовок X-Sharer-User-Id")
    void create_missingHeader_400() throws Exception {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Нужна дрель");

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /requests — 200, корректный запрос проксируется в client")
    void create_ok_forwarded() throws Exception {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Нужна дрель");

        Mockito.when(client.createRequest(eq(1L), any(ItemRequestCreateDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of("id", 123, "description", "Нужна дрель")));

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_HEADER, 1)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(123)))
                .andExpect(jsonPath("$.description", is("Нужна дрель")));
    }

    @Test
    @DisplayName("GET /requests — 400, если нет заголовка пользователя")
    void getOwn_missingHeader_400() throws Exception {
        mockMvc.perform(get("/requests"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /requests — 200, корректно проксируется")
    void getOwn_ok_forwarded() throws Exception {
        Mockito.when(client.getOwnRequests(5L))
                .thenReturn(ResponseEntity.ok(new Object[]{}));

        mockMvc.perform(get("/requests")
                        .header(USER_HEADER, 5))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /requests/all — 400, size < 1")
    void getAll_sizeLessThanOne_400() throws Exception {
        mockMvc.perform(get("/requests/all")
                        .header(USER_HEADER, 1)
                        .param("from", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /requests/all — 400, from < 0")
    void getAll_fromNegative_400() throws Exception {
        mockMvc.perform(get("/requests/all")
                        .header(USER_HEADER, 1)
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /requests/all — 400, если нет заголовка пользователя")
    void getAll_missingHeader_400() throws Exception {
        mockMvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /requests/all — 200, корректная пагинация")
    void getAll_ok_forwarded() throws Exception {
        Mockito.when(client.getAllRequests(eq(2L), eq(0), eq(10)))
                .thenReturn(ResponseEntity.ok(new Object[]{}));

        mockMvc.perform(get("/requests/all")
                        .header(USER_HEADER, 2)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /requests/{id} — 400, если нет заголовка пользователя")
    void getById_missingHeader_400() throws Exception {
        mockMvc.perform(get("/requests/42"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /requests/{id} — 200, корректный запрос проксируется")
    void getById_ok_forwarded() throws Exception {
        Mockito.when(client.getRequestById(3L, 42L))
                .thenReturn(ResponseEntity.ok(Map.of("id", 42, "description", "Любая")));

        mockMvc.perform(get("/requests/42")
                        .header(USER_HEADER, 3))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(42)));
    }
}