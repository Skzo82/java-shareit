package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")

@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    // POST /bookings
    @PostMapping
    public BookingResponseDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestBody @Valid BookingRequestDto dto) {
        // // валидация в сервисе
        return bookingService.addBooking(userId, dto);
    }

    // PATCH /bookings/{bookingId}?approved={true|false}
    @PatchMapping("/{bookingId}")
    public BookingResponseDto approve(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                      @PathVariable Long bookingId,
                                      @RequestParam boolean approved) {
        // // только владелец вещи может подтвердить/отклонить
        return bookingService.approveBooking(ownerId, bookingId, approved);
    }

    // GET /bookings/{bookingId}
    @GetMapping("/{bookingId}")
    public BookingResponseDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable Long bookingId) {
        // // доступ для автора бронирования или владельца вещи
        return bookingService.getBooking(userId, bookingId);
    }

    // GET /bookings?state=...
    @GetMapping
    public List<BookingResponseDto> getForUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(required = false) String state,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "10") int size) {
        // // отфильтрованные бронирования пользователя
        return bookingService.getUserBookings(userId, state, from, size);
    }

    // GET /bookings/owner?state=...
    @GetMapping("/owner")
    public List<BookingResponseDto> getForOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                @RequestParam(required = false) String state,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size) {
        // // отфильтрованные бронирования для всех вещей владельца
        return bookingService.getOwnerBookings(ownerId, state, from, size);
    }
}

