package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.http.WebMvcTestSupport;

@WebMvcTest(ItemController.class)
@WebMvcTestSupport
class ItemControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemClient client;

    @Test
    void contextLoads() {
    }
}