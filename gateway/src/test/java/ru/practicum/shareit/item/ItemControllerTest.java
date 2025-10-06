package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.ItemCreateDto;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemClient client;

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Test
    @DisplayName("POST /items con requestId → inoltra e 200")
    void create_withRequestId_ok() throws Exception {
        ItemCreateDto dto = ItemCreateDto.builder()
                .name("Smerigliatrice")
                .description("750W")
                .available(true)
                .requestId(2L)
                .build();

        Mockito.when(client.create(eq(1L), any(ItemCreateDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of("id", 10, "name", "Smerigliatrice", "requestId", 2)));

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_HEADER, 1)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)));
    }

    @Test
    @DisplayName("POST /items senza requestId → inoltra e 200")
    void create_withoutRequestId_ok() throws Exception {
        ItemCreateDto dto = ItemCreateDto.builder()
                .name("Scala")
                .description("3m")
                .available(true)
                .build();

        Mockito.when(client.create(eq(1L), any(ItemCreateDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of("id", 11, "name", "Scala", "requestId", null)));

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, 1)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(11)));
    }

    @Test
    @DisplayName("POST /items → 400 se name/description vuoti o available null")
    void create_validationErrors_400() throws Exception {
        ItemCreateDto invalid = ItemCreateDto.builder()
                .name("  ")
                .description("")
                .available(null)
                .build();

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, 1)
                        .content(mapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }
}