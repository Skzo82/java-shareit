package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class ItemRequestController {

    private static final String USER_HEADER = "X-Sharer-User-Id"; // Имя заголовка

    private final ItemRequestClient client;

    // POST /requests
    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_HEADER) @Positive Long userId,
                                         @Valid @RequestBody ItemRequestCreateDto dto) {
        // Валидация в gateway -> проксирование на сервер
        return client.createRequest(userId, dto);
    }

    // GET /requests (собственные запросы)
    @GetMapping
    public ResponseEntity<Object> getOwn(@RequestHeader(USER_HEADER) @Positive Long userId) {
        return client.getOwnRequests(userId);
    }

    // GET /requests/all
    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader(USER_HEADER) @Positive Long userId,
                                         @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                         @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        return client.getAllRequests(userId, from, size);
    }

    // GET /requests/{requestId}
    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader(USER_HEADER) @Positive Long userId,
                                          @PathVariable @Positive Long requestId) {
        return client.getRequestById(userId, requestId);
    }
}
