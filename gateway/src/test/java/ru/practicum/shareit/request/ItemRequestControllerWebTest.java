package ru.practicum.shareit.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.http.WebMvcTestSupport;

@WebMvcTest(ItemRequestController.class)
@WebMvcTestSupport
class ItemRequestControllerWebTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ItemRequestClient itemRequestClient;

    @Test
    @DisplayName("il contesto si avvia")
    void contextLoads() {
    }

}
