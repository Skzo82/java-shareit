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

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    private static final String USER_HEADER = "X-Sharer-User-Id";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemRequestClient client;

    @Test
    @DisplayName("POST /requests -> 200 и перенаправление запроса на сервер")
    void create_shouldForwardAndReturnOk() throws Exception {
        // given — подготавливаем DTO и мок клиента
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Ищу шлифовальную машину");

        // Мокаем ответ клиента, чтобы вернуть фейковые данные
        Mockito.when(client.createRequest(eq(1L), any(ItemRequestCreateDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of("id", 10, "description", "Ищу шлифовальную машину")));

        // when / then — проверяем результат
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_HEADER, 1)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.description", is("Ищу шлифовальную машину")));
    }

    @Test
    @DisplayName("POST /requests -> 400 если description пустая (валидация на gateway)")
    void create_shouldValidateDescription() throws Exception {
        // given — создаем DTO с пустым описанием
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription(" "); // пустая строка

        // when / then — ожидаем 400 Bad Request
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, 1)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /requests -> 200 и перенаправление с заголовком пользователя")
    void getOwn_shouldForward() throws Exception {
        // given — мокаем пустой ответ клиента
        Mockito.when(client.getOwnRequests(1L))
                .thenReturn(ResponseEntity.ok(new Object[]{}));

        // when / then
        mockMvc.perform(get("/requests")
                        .header(USER_HEADER, 1))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /requests/all -> 200 с пагинацией и перенаправлением")
    void getAll_shouldForwardWithPagination() throws Exception {
        // given
        Mockito.when(client.getAllRequests(eq(2L), eq(0), eq(5)))
                .thenReturn(ResponseEntity.ok(new Object[]{}));

        // when / then
        mockMvc.perform(get("/requests/all")
                        .header(USER_HEADER, 2)
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /requests/all -> 400 если size < 1 (валидация)")
    void getAll_shouldValidateSize() throws Exception {
        // when / then
        mockMvc.perform(get("/requests/all")
                        .header(USER_HEADER, 2)
                        .param("from", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /requests/{id} -> 200 и перенаправление на сервер")
    void getById_shouldForward() throws Exception {
        // given
        Mockito.when(client.getRequestById(1L, 99L))
                .thenReturn(ResponseEntity.ok(Map.of("id", 99)));

        // when / then
        mockMvc.perform(get("/requests/99")
                        .header(USER_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(99)));
    }

    @Test
    @DisplayName("GET /requests -> 400 если отсутствует X-Sharer-User-Id")
    void getOwn_shouldFailWithoutHeader() throws Exception {
        // when / then — проверяем отсутствие обязательного заголовка
        mockMvc.perform(get("/requests"))
                .andExpect(status().isBadRequest());
    }
}