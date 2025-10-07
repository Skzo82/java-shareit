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
class BookingControllerWebTest {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingClient client;

    @Test
    @DisplayName("POST /bookings — 400 se manca X-Sharer-User-Id")
    void create_missingHeader_400() throws Exception {
        String json = mapper.writeValueAsString(Map.of(
                "itemId", 1,
                "start", LocalDateTime.now().plusDays(1).toString(),
                "end", LocalDateTime.now().plusDays(2).toString()
        ));

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /bookings — 200, delega al client")
    void create_ok_forwarded() throws Exception {
        String json = mapper.writeValueAsString(Map.of(
                "itemId", 1,
                "start", LocalDateTime.now().plusDays(1).toString(),
                "end", LocalDateTime.now().plusDays(2).toString()
        ));

        Mockito.when(client.create(eq(7L), any()))
                .thenReturn(ResponseEntity.ok(Map.of("id", 100)));

        mockMvc.perform(post("/bookings")
                        .header(USER_HEADER, 7)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(100)));
    }

    @Test
    @DisplayName("PATCH /bookings/{id}?approved=true — 200, delega al client")
    void approve_ok_forwarded() throws Exception {
        Mockito.when(client.approve(1L, 10L, true))
                .thenReturn(ResponseEntity.ok(Map.of("id", 10, "approved", true)));

        mockMvc.perform(patch("/bookings/10")
                        .header(USER_HEADER, 1)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.approved", is(true)));
    }

    @Test
    @DisplayName("GET /bookings — 200, lista per booker")
    void getAllByUser_ok_forwarded() throws Exception {
        Mockito.when(client.getAllByUser(1L, "ALL", 0, 10))
                .thenReturn(ResponseEntity.ok(new Object[]{}));

        mockMvc.perform(get("/bookings")
                        .header(USER_HEADER, 1)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /bookings/owner — 200, lista per owner")
    void getAllByOwner_ok_forwarded() throws Exception {
        Mockito.when(client.getAllByOwner(2L, "ALL", 0, 10))
                .thenReturn(ResponseEntity.ok(new Object[]{}));

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_HEADER, 2)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /bookings/{id} — 200, delega al client")
    void getById_ok_forwarded() throws Exception {
        Mockito.when(client.getById(3L, 77L))
                .thenReturn(ResponseEntity.ok(Map.of("id", 77)));

        mockMvc.perform(get("/bookings/77")
                        .header(USER_HEADER, 3))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(77)));
    }
}