package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestClient client;

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Test
    @DisplayName("POST /requests -> 200 e inoltro al server")
    void create_shouldForwardAndReturnOk() throws Exception {
        // given
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Cerco smerigliatrice");

        // Мокаем ответ клиента, чтобы вернуть заглушку JSON
        Mockito.when(client.createRequest(eq(1L), any(ItemRequestCreateDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of("id", 10, "description", "Cerco smerigliatrice")));

        // when / then
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_HEADER, 1)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.description", is("Cerco smerigliatrice")));
    }

    @Test
    @DisplayName("POST /requests -> 400 se description blank (validazione nel gateway)")
    void create_shouldValidateDescription() throws Exception {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("  "); // пустая строка

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, 1)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /requests -> 200 e inoltra con header utente")
    void getOwn_shouldForward() throws Exception {
        Mockito.when(client.getOwnRequests(1L))
                .thenReturn(ResponseEntity.ok(new Object[]{}));

        mockMvc.perform(get("/requests")
                        .header(USER_HEADER, 1))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /requests/all -> 200 paginazione e inoltro")
    void getAll_shouldForwardWithPagination() throws Exception {
        Mockito.when(client.getAllRequests(eq(2L), eq(0), eq(5)))
                .thenReturn(ResponseEntity.ok(new Object[]{}));

        mockMvc.perform(get("/requests/all")
                        .header(USER_HEADER, 2)
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /requests/all -> 400 se size < 1 (validazione)")
    void getAll_shouldValidateSize() throws Exception {
        mockMvc.perform(get("/requests/all")
                        .header(USER_HEADER, 2)
                        .param("from", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /requests/{id} -> 200 e inoltro")
    void getById_shouldForward() throws Exception {
        Mockito.when(client.getRequestById(1L, 99L))
                .thenReturn(ResponseEntity.ok(Map.of("id", 99)));

        mockMvc.perform(get("/requests/99")
                        .header(USER_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(99)));
    }

    @Test
    @DisplayName("GET /requests -> 400 se manca X-Sharer-User-Id")
    void getOwn_shouldFailWithoutHeader() throws Exception {
        mockMvc.perform(get("/requests"))
                .andExpect(status().isBadRequest()); // обязательный заголовок отсутствует
    }
}
