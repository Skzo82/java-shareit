package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.error.GlobalExceptionHandler;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@Import({
        GlobalExceptionHandler.class,
        org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration.class,
        org.springframework.validation.beanvalidation.MethodValidationPostProcessor.class
})
class ItemControllerValidationTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;

    @MockBean
    ItemService itemService;

    @Test
    @DisplayName("POST /items - 400 se name/description/available non validi")
    void create_shouldReturn400_onInvalidBody() throws Exception {
        ItemDto bad = new ItemDto();
        bad.setName("  ");
        bad.setDescription("");
        bad.setAvailable(null);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("POST /items - 200 su body valido")
    void create_shouldReturn200_onValidBody() throws Exception {
        ItemDto ok = new ItemDto();
        ok.setName("Drill");
        ok.setDescription("Bosch");
        ok.setAvailable(true);

        ItemDto saved = new ItemDto();
        saved.setId(10L);
        saved.setName(ok.getName());
        saved.setDescription(ok.getDescription());
        saved.setAvailable(ok.getAvailable());

        Mockito.when(itemService.create(eq(1L), any(ItemDto.class))).thenReturn(saved);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(ok)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.name").value("Drill"));
    }

    @Test
    @DisplayName("GET /items - 400 se from < 0")
    void getByOwner_should400_whenFromNegative() throws Exception {
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /items - 400 se size <= 0")
    void getByOwner_should400_whenSizeInvalid() throws Exception {
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /items - 200 su parametri validi")
    void getByOwner_should200_whenParamsOk() throws Exception {
        Mockito.when(itemService.getItemsByOwner(eq(1L), eq(0), eq(2)))
                .thenReturn(List.of());

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /items/search - 400 se from < 0 o size <= 0")
    void search_shouldValidatePaginationParams() throws Exception {
        mvc.perform(get("/items/search")
                        .param("text", "drill")
                        .param("from", "-5")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/items/search")
                        .param("text", "drill")
                        .param("from", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /items/{id} - 200 con body parziale (no @Valid)")
    void update_shouldAllowPartialBody() throws Exception {
        ItemDto patch = new ItemDto();
        patch.setDescription("new desc");

        ItemDto returned = new ItemDto();
        returned.setId(5L);
        returned.setName("keep");
        returned.setDescription("new desc");
        returned.setAvailable(true);

        Mockito.when(itemService.update(eq(1L), any(ItemDto.class))).thenReturn(returned);

        mvc.perform(patch("/items/{itemId}", 5)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(patch)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.description").value("new desc"));
    }
}
