package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.ItemCreateDto;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    private static final String USER_HEADER = "X-Sharer-User-Id";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemClient client;

    @Test
    @DisplayName("POST /items с requestId → должен вернуть 200 и проксировать запрос в сервер")
    void create_withRequestId_ok() throws Exception {
        // Подготавливаем DTO с непустыми полями и requestId
        ItemCreateDto dto = ItemCreateDto.builder()
                .name("Шлифовальная машина")
                .description("750 Вт")
                .available(true)
                .requestId(2L)
                .build();

        // Мокаем ответ клиента — возвращаем JSON с id и requestId
        Mockito.when(client.create(eq(1L), any(ItemCreateDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 10,
                        "name", "Шлифовальная машина",
                        "requestId", 2
                )));

        // Отправляем POST-запрос и проверяем, что возвращается 200 и правильный id
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_HEADER, 1)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)));
    }

    @Test
    @DisplayName("POST /items без requestId → должен вернуть 200 и проксировать запрос")
    void create_withoutRequestId_ok() throws Exception {
        // DTO без поля requestId
        ItemCreateDto dto = ItemCreateDto.builder()
                .name("Лестница")
                .description("3 метра")
                .available(true)
                .build();

        // Map.of не поддерживает null — используем HashMap
        Map<String, Object> body = new HashMap<>();
        body.put("id", 11);
        body.put("name", "Лестница");
        body.put("requestId", null);

        // Мокаем ответ клиента
        Mockito.when(client.create(eq(1L), any(ItemCreateDto.class)))
                .thenReturn(ResponseEntity.ok(body));

        // Проверяем успешный ответ (200)
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, 1)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(11)));
    }

    @Test
    @DisplayName("POST /items → должен вернуть 400, если поля name/description пустые или available = null")
    void create_validationErrors_400() throws Exception {
        // DTO с пустыми и некорректными значениями
        ItemCreateDto invalid = ItemCreateDto.builder()
                .name(" ")
                .description("")
                .available(null)
                .build();

        // Проверяем, что валидация отрабатывает и возвращает 400
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, 1)
                        .content(mapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }
}