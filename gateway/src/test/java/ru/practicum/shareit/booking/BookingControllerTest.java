package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingClient client;

    @Test
    @DisplayName("POST /bookings — 200: корректный запрос проксируется")
    void create_ok_forwarded() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingRequestDto dto = BookingRequestDto.builder()
                .itemId(5L)
                .start(start)
                .end(end)
                .build();

        Mockito.when(client.create(eq(1L), any(BookingRequestDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of("id", 101, "itemId", 5)));

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_HEADER, 1)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(101)))
                .andExpect(jsonPath("$.itemId", is(5)));
    }

    @Test
    @DisplayName("POST /bookings — 400: отсутствует X-Sharer-User-Id")
    void create_missingHeader_400() throws Exception {
        BookingRequestDto dto = BookingRequestDto.builder()
                .itemId(5L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /bookings — 400: отсутствуют start/end")
    void create_invalidDates_400() throws Exception {
        BookingRequestDto dto = BookingRequestDto.builder()
                .itemId(5L)
                .start(null)
                .end(null)
                .build();

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, 1)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /bookings/{id}?approved=true — 200")
    void approve_ok_forwarded() throws Exception {
        Mockito.when(client.approve(2L, 99L, true))
                .thenReturn(ResponseEntity.ok(Map.of("id", 99, "status", "APPROVED")));

        mockMvc.perform(patch("/bookings/99")
                        .header(USER_HEADER, 2)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(99)))
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    @DisplayName("PATCH /bookings/{id} — 400: нет заголовка пользователя")
    void approve_missingHeader_400() throws Exception {
        mockMvc.perform(patch("/bookings/1").param("approved", "true"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /bookings/{id} — 200")
    void getById_ok_forwarded() throws Exception {
        Mockito.when(client.getById(3L, 42L))
                .thenReturn(ResponseEntity.ok(Map.of("id", 42, "bookerId", 3)));

        mockMvc.perform(get("/bookings/42").header(USER_HEADER, 3))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(42)));
    }

    @Test
    @DisplayName("GET /bookings/{id} — 400: нет заголовка")
    void getById_missingHeader_400() throws Exception {
        mockMvc.perform(get("/bookings/42"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /bookings — 200")
    void getAllUser_ok_forwarded() throws Exception {
        Mockito.when(client.getAllByUser(4L, "ALL", 0, 5))
                .thenReturn(ResponseEntity.ok(new Object[]{}));

        mockMvc.perform(get("/bookings")
                        .header(USER_HEADER, 4)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /bookings — 400: пагинация некорректна")
    void getAllUser_invalidPagination_400() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header(USER_HEADER, 4)
                        .param("state", "ALL")
                        .param("from", "-1")
                        .param("size", "5"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/bookings")
                        .header(USER_HEADER, 4)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /bookings — 400: нет заголовка пользователя")
    void getAllUser_missingHeader_400() throws Exception {
        mockMvc.perform(get("/bookings").param("state", "ALL"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /bookings/owner — 200")
    void getAllOwner_ok_forwarded() throws Exception {
        Mockito.when(client.getAllByOwner(5L, "CURRENT", 0, 10))
                .thenReturn(ResponseEntity.ok(new Object[]{}));

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_HEADER, 5)
                        .param("state", "CURRENT")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /bookings/owner — 400: нет заголовка пользователя")
    void getAllOwner_missingHeader_400() throws Exception {
        mockMvc.perform(get("/bookings/owner").param("state", "CURRENT"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /bookings/owner — 400: пагинация некорректна")
    void getAllOwner_invalidPagination_400() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header(USER_HEADER, 5)
                        .param("state", "CURRENT")
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_HEADER, 5)
                        .param("state", "CURRENT")
                        .param("from", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
    }
}
