package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    private final ItemClient client;

    // POST /items (с опциональным requestId)
    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_HEADER) @Positive Long userId,
                                         @Valid @RequestBody ItemCreateDto dto) {
        return client.create(userId, dto);
    }

    // PATCH /items/{id}
    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@RequestHeader(USER_HEADER) @Positive Long userId,
                                         @PathVariable("id") @Positive Long itemId,
                                         @Valid @RequestBody ItemUpdateDto dto) {
        return client.update(userId, itemId, dto);
    }
}
