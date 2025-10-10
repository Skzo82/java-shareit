package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.http.Headers;

@Validated
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingClient client;

    @GetMapping("/ping")
    public ResponseEntity<Object> ping(@RequestHeader(Headers.USER_ID) @Positive long userId) {
        return client.getAllByUser(userId, "ALL", 0, 1);
    }

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(Headers.USER_ID) Long userId,
            @Valid @RequestBody BookingRequestDto dto) {
        return client.create(userId, dto); // pass-through
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader(Headers.USER_ID) @Positive Long userId,
                                          @PathVariable @Positive Long bookingId,
                                          @RequestParam boolean approved) {
        return client.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader(Headers.USER_ID) @Positive Long userId,
                                          @PathVariable @Positive Long bookingId) {
        return client.getById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUser(@RequestHeader(Headers.USER_ID) @Positive Long userId,
                                               @RequestParam(defaultValue = "ALL") String state,
                                               @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                               @RequestParam(defaultValue = "10") @Positive int size) {
        return client.getAllByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(@RequestHeader(Headers.USER_ID) @Positive Long userId,
                                                @RequestParam(defaultValue = "ALL") String state,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "10") @Positive int size) {
        return client.getAllByOwner(userId, state, from, size);
    }
}