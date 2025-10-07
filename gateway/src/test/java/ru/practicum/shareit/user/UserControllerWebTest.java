package ru.practicum.shareit.user;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerWebTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserClient client;

    @Test
    @DisplayName("POST /users — 200, crea utente e delega al client")
    void create_ok_forwarded() throws Exception {
        String json = mapper.writeValueAsString(Map.of("name", "Mario", "email", "mario@ex.it"));

        Mockito.when(client.create(any()))
                .thenReturn(ResponseEntity.ok(Map.of("id", 10, "name", "Mario")));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)));
    }

    @Test
    @DisplayName("GET /users — 200, lista utenti delegata al client")
    void getAll_ok_forwarded() throws Exception {
        Mockito.when(client.getAll())
                .thenReturn(ResponseEntity.ok(new Object[]{}));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /users/{id} — 200, delega al client")
    void getById_ok_forwarded() throws Exception {
        Mockito.when(client.getById(5L))
                .thenReturn(ResponseEntity.ok(Map.of("id", 5, "name", "Test")));

        mockMvc.perform(get("/users/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)));
    }

    @Test
    @DisplayName("PATCH /users/{id} — 200, aggiorna parzialmente e delega")
    void patch_ok_forwarded() throws Exception {
        String json = mapper.writeValueAsString(Map.of("name", "Nuovo Nome"));

        Mockito.when(client.update(eq(12L), any()))
                .thenReturn(ResponseEntity.ok(Map.of("id", 12, "name", "Nuovo Nome")));

        mockMvc.perform(patch("/users/12")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(12)))
                .andExpect(jsonPath("$.name", is("Nuovo Nome")));
    }

    @Test
    @DisplayName("DELETE /users/{id} — 200, delega al client")
    void delete_ok_forwarded() throws Exception {
        Mockito.when(client.delete(9L))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(delete("/users/9"))
                .andExpect(status().isOk());
    }
}