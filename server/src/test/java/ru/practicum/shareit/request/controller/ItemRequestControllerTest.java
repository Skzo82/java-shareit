package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService service;

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Test
    @DisplayName("POST /requests -> 200 e ritorna DTO dal service")
    void create_shouldReturnDto() throws Exception {
        ItemRequestCreateDto in = new ItemRequestCreateDto();
        in.setDescription("Serve tassellatore");

        ItemRequestResponseDto out = ItemRequestResponseDto.builder()
                .id(7L)
                .description("Serve tassellatore")
                .created(LocalDateTime.now())
                .items(List.of())
                .build();

        Mockito.when(service.create(eq(1L), any(ItemRequestCreateDto.class)))
                .thenReturn(out);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, 1)
                        .content(mapper.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(7)))
                .andExpect(jsonPath("$.description", is("Serve tassellatore")));
    }

    @Test
    @DisplayName("GET /requests -> 200 e ritorna lista dei propri")
    void getOwn_shouldReturnList() throws Exception {
        ItemRequestResponseDto dto = ItemRequestResponseDto.builder()
                .id(2L)
                .description("Cerco martello")
                .created(LocalDateTime.now())
                .items(List.of(ItemShortDto.builder().id(10L).name("Martello").ownerId(5L).build()))
                .build();

        Mockito.when(service.getOwn(2L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/requests")
                        .header(USER_HEADER, 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].name", is("Martello")));
    }

    @Test
    @DisplayName("GET /requests/all -> 200 con paginazione")
    void getAll_shouldReturnPage() throws Exception {
        ItemRequestResponseDto first = ItemRequestResponseDto.builder()
                .id(5L).description("Req A").created(LocalDateTime.now()).items(List.of()).build();
        ItemRequestResponseDto second = ItemRequestResponseDto.builder()
                .id(3L).description("Req B").created(LocalDateTime.now().minusDays(1)).items(List.of()).build();

        Mockito.when(service.getAll(1L, 0, 2)).thenReturn(List.of(first, second));

        mockMvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "2")
                        .header(USER_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(5)))
                .andExpect(jsonPath("$[1].id", is(3)));
    }

    @Test
    @DisplayName("GET /requests/{id} -> 200 e ritorna la richiesta")
    void getById_shouldReturnOne() throws Exception {
        ItemRequestResponseDto dto = ItemRequestResponseDto.builder()
                .id(9L).description("Dettaglio").created(LocalDateTime.now()).items(List.of()).build();

        Mockito.when(service.getById(1L, 9L)).thenReturn(dto);

        mockMvc.perform(get("/requests/9")
                        .header(USER_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(9)))
                .andExpect(jsonPath("$.description", is("Dettaglio")));
    }
}