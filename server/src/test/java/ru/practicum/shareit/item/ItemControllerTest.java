package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;

    @MockBean
    ItemService itemService;

    private static final String H = "X-Sharer-User-Id";

    @Test
    void create_withRequestId_ok() throws Exception {
        ItemDto in = new ItemDto();
        in.setName("Smerigliatrice");
        in.setDescription("750W");
        in.setAvailable(true);
        in.setRequestId(2L);

        ItemDto out = new ItemDto();
        out.setId(10L);
        out.setName("Smerigliatrice");
        out.setDescription("750W");
        out.setAvailable(true);
        out.setRequestId(2L);

        Mockito.when(itemService.create(eq(1L), any(ItemDto.class))).thenReturn(out);

        mvc.perform(post("/items")
                        .header(H, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.requestId").value(2));
    }

    @Test
    void create_withoutRequestId_ok() throws Exception {
        ItemDto in = new ItemDto();
        in.setName("Scala");
        in.setDescription("3m");
        in.setAvailable(true);

        ItemDto out = new ItemDto();
        out.setId(11L);
        out.setName("Scala");
        out.setDescription("3m");
        out.setAvailable(true);

        Mockito.when(itemService.create(eq(1L), any(ItemDto.class))).thenReturn(out);

        mvc.perform(post("/items")
                        .header(H, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(11))
                .andExpect(jsonPath("$.requestId").doesNotExist());
    }
}
