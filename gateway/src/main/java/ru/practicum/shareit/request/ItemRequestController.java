package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.http.Headers;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class ItemRequestController {

    private final ItemRequestClient client;

    @GetMapping("/me")
    public ResponseEntity<Object> me(@RequestHeader(Headers.USER_ID) @Positive long userId) {
        return client.getOwnRequests(userId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(Headers.USER_ID) @Positive Long userId,
                                         @Valid @RequestBody ItemRequestCreateDto dto) {
        return client.createRequest(userId, dto);
    }

    @GetMapping
    public ResponseEntity<Object> getOwn(@RequestHeader(Headers.USER_ID) @Positive Long userId) {
        return client.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader(Headers.USER_ID) @Positive Long userId,
                                         @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                         @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        return client.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader(Headers.USER_ID) @Positive Long userId,
                                          @PathVariable @Positive Long requestId) {
        return client.getRequestById(userId, requestId);
    }
}