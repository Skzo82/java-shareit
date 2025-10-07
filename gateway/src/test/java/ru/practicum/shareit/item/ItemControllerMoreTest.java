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
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ItemController.class)
class ItemControllerMoreTest {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemClient client;

    // ---------- CREATE ----------

    @Test
    @DisplayName("POST /items — 400, если нет заголовка X-Sharer-User-Id")
    void create_missingHeader_400() throws Exception {
        ItemCreateDto dto = ItemCreateDto.builder()
                .name("Стремянка")
                .description("3м")
                .available(true)
                .build();

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /items — 400, отдельные ошибки валидации (name/description/available)")
    void create_separateValidation_400() throws Exception {
        // name пустой
        ItemCreateDto noName = ItemCreateDto.builder()
                .name(" ")
                .description("ok")
                .available(true)
                .build();
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, 1)
                        .content(mapper.writeValueAsString(noName)))
                .andExpect(status().isBadRequest());

        // description пустой
        ItemCreateDto noDesc = ItemCreateDto.builder()
                .name("Лестница")
                .description(" ")
                .available(true)
                .build();
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, 1)
                        .content(mapper.writeValueAsString(noDesc)))
                .andExpect(status().isBadRequest());

        // available null
        ItemCreateDto noAvail = ItemCreateDto.builder()
                .name("Лестница")
                .description("3м")
                .available(null)
                .build();
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, 1)
                        .content(mapper.writeValueAsString(noAvail)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /items — 200, корректный запрос проксируется в client")
    void create_ok_forwarded() throws Exception {
        ItemCreateDto dto = ItemCreateDto.builder()
                .name("Дрель")
                .description("750W")
                .available(true)
                .requestId(7L)
                .build();

        Mockito.when(client.create(eq(10L), any(ItemCreateDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of("id", 100, "name", "Дрель", "requestId", 7)));

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_HEADER, 10)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(100)))
                .andExpect(jsonPath("$.name", is("Дрель")));
    }

    // ---------- GET by id ----------

    @Test
    @DisplayName("GET /items/{id} — 400 при отсутствии заголовка пользователя")
    void getById_missingHeader_400() throws Exception {
        mockMvc.perform(get("/items/5"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /items/{id} — 200, проксирование в client")
    void getById_ok_forwarded() throws Exception {
        // предполагаем метод client.getById(userId, itemId)
        Mockito.when(client.getById(1L, 5L))
                .thenReturn(ResponseEntity.ok(Map.of("id", 5, "name", "Что-то")));

        mockMvc.perform(get("/items/5")
                        .header(USER_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)));
    }

    // ---------- GET владельца (пагинация) ----------

    @Test
    @DisplayName("GET /items — 400, если нет заголовка пользователя")
    void getOwnerItems_missingHeader_400() throws Exception {
        mockMvc.perform(get("/items")
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /items — 400, from < 0 или size < 1")
    void getOwnerItems_validation_400() throws Exception {
        mockMvc.perform(get("/items")
                        .header(USER_HEADER, 2)
                        .param("from", "-1")
                        .param("size", "5"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/items")
                        .header(USER_HEADER, 2)
                        .param("from", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /items — 200, корректная пагинация проксируется")
    void getOwnerItems_ok_forwarded() throws Exception {
        // предполагаем метод client.getAll(userId, from, size)
        Mockito.when(client.getAll(2L, 0, 5))
                .thenReturn(ResponseEntity.ok(new Object[]{}));

        mockMvc.perform(get("/items")
                        .header(USER_HEADER, 2)
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(status().isOk());
    }

    // ---------- Поиск ----------

    @Test
    @DisplayName("GET /items/search — 400, если нет заголовка пользователя")
    void search_missingHeader_400() throws Exception {
        mockMvc.perform(get("/items/search")
                        .param("text", "дрель")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /items/search — 400, from < 0 или size < 1")
    void search_validation_400() throws Exception {
        mockMvc.perform(get("/items/search")
                        .header(USER_HEADER, 3)
                        .param("text", "дрель")
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/items/search")
                        .header(USER_HEADER, 3)
                        .param("text", "дрель")
                        .param("from", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /items/search — 200, корректный запрос проксируется")
    void search_ok_forwarded() throws Exception {
        // предполагаем метод client.search(userId, text, from, size)
        Mockito.when(client.search(3L, "дрель", 0, 10))
                .thenReturn(ResponseEntity.ok(new Object[]{}));

        mockMvc.perform(get("/items/search")
                        .header(USER_HEADER, 3)
                        .param("text", "дрель")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    // ---------- Комментарии ----------

    @Test
    @DisplayName("POST /items/{id}/comment — 400, если нет заголовка пользователя")
    void addComment_missingHeader_400() throws Exception {
        CommentCreateDto dto = CommentCreateDto.builder().text("Хорошая вещь").build();

        mockMvc.perform(post("/items/9/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /items/{id}/comment — 400, если текст пустой")
    void addComment_blank_400() throws Exception {
        CommentCreateDto dto = CommentCreateDto.builder().text(" ").build();

        mockMvc.perform(post("/items/9/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, 4)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /items/{id}/comment — 200, corretto inoltro al client")
    void addComment_ok_forwarded() throws Exception {
        // arrange
        Mockito.when(client.addComment(eq(4L), eq(9L), any(CommentCreateDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of("id", 77, "text", "Отлично!")));

        // act + assert
        mockMvc.perform(post("/items/9/comment")
                        .header(USER_HEADER, 4)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content("{\"text\":\"Отлично!\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(77)))
                .andExpect(jsonPath("$.text", is("Отлично!")));
    }

    // ---------- PATCH (обновление) ----------

    @Test
    @DisplayName("PATCH /items/{id} — 400, если нет заголовка пользователя")
    void patch_missingHeader_400() throws Exception {
        String patchJson = mapper.writeValueAsString(Map.of(
                "name", "Новое имя",
                "description", "Обновлено",
                "available", true
        ));

        mockMvc.perform(patch("/items/12")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /items/{id} — 200, частичное обновление проксируется")
    void patch_ok_forwarded() throws Exception {
        String patchJson = mapper.writeValueAsString(Map.of(
                "name", "Новое имя",
                "description", "Обновлено",
                "available", true
        ));

        Mockito.when(client.update(eq(1L), eq(12L), any()))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 12,
                        "name", "Новое имя"
                )));

        mockMvc.perform(patch("/items/12")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(12)))
                .andExpect(jsonPath("$.name", is("Новое имя")));
    }
