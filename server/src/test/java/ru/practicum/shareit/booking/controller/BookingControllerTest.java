package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.ItemShortDto;
import ru.practicum.shareit.booking.dto.UserShortDto;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;

    @MockBean
    BookingService service;

    private static final String H = "X-Sharer-User-Id";

    @Test
    void create_ok() throws Exception {
        BookingRequestDto in = new BookingRequestDto();
        in.setItemId(3L);
        in.setStart(LocalDateTime.now().plusDays(1));
        in.setEnd(LocalDateTime.now().plusDays(2));

        BookingResponseDto out = new BookingResponseDto(
                42L, in.getStart(), in.getEnd(), BookingStatus.WAITING,
                new UserShortDto(1L), new ItemShortDto(3L, "Лестница")
        );

        Mockito.when(service.addBooking(eq(1L), any(BookingRequestDto.class))).thenReturn(out);

        mvc.perform(post("/bookings")
                        .header(H, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void approve_ok() throws Exception {
        BookingResponseDto out = new BookingResponseDto(
                7L, LocalDateTime.now(), LocalDateTime.now().plusHours(2),
                BookingStatus.APPROVED, new UserShortDto(2L), new ItemShortDto(1L, "Дрель")
        );
        Mockito.when(service.approveBooking(1L, 7L, true)).thenReturn(out);

        mvc.perform(patch("/bookings/7")
                        .header(H, 1)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getById_ok() throws Exception {
        BookingResponseDto out = new BookingResponseDto(
                5L, LocalDateTime.now(), LocalDateTime.now().plusHours(1),
                BookingStatus.APPROVED, new UserShortDto(1L), new ItemShortDto(3L, "Лестница")
        );
        Mockito.when(service.getBooking(1L, 5L)).thenReturn(out);

        mvc.perform(get("/bookings/5").header(H, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));
    }
}
