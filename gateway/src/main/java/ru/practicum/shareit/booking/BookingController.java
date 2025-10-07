package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

@Validated
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final BookingClient client;

    /**
     * Создать бронирование
     */
    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(USER_HEADER) Long userId,
            @Valid @RequestBody BookItemRequestDto dto
    ) {
        return client.create(userId, dto);
    }

    /**
     * Одобрить/отклонить бронирование владельцем вещи
     */
    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable Long bookingId,
            @RequestParam boolean approved
    ) {
        return client.approve(userId, bookingId, approved);
    }

    /**
     * Получить бронирование по id (доступно владельцу/бронирующему)
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable Long bookingId
    ) {
        return client.getById(userId, bookingId);
    }

    /**
     * Список бронирований текущего пользователя с пагинацией
     */
    @GetMapping
    public ResponseEntity<Object> getAllByUser(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size
    ) {
        return client.getAllByUser(userId, state, from, size);
    }

    /**
     * Список бронирований для вещей владельца с пагинацией
     */
    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size
    ) {
        return client.getAllByOwner(userId, state, from, size);
    }
}