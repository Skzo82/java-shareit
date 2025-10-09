package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.http.Headers;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> create(
            @RequestHeader(value = Headers.USER_ID, required = false) Long userId,
            @RequestBody ItemCreateDto dto
    ) {
        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing X-Sharer-User-Id header"));
        }
        if (dto == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Request body is required"));
        }
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Item name must not be blank"));
        }
        if (dto.getDescription() == null || dto.getDescription().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Item description must not be blank"));
        }
        if (dto.getAvailable() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Item availability must be specified"));
        }

        return itemClient.create(userId, dto);
    }

    @PatchMapping(path = "/{itemId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> update(
            @RequestHeader(value = Headers.USER_ID, required = false) Long userId,
            @PathVariable Long itemId,
            @RequestBody ItemUpdateDto dto
    ) {
        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing X-Sharer-User-Id header"));
        }
        return itemClient.update(userId, itemId, dto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(
            @RequestHeader(value = Headers.USER_ID, required = false) Long userId,
            @PathVariable Long itemId
    ) {
        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing X-Sharer-User-Id header"));
        }
        return itemClient.getById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnerItems(
            @RequestHeader(Headers.USER_ID) Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        if (from == null || from < 0 || size == null || size <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid pagination"));
        }
        return itemClient.getOwnerItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(
            @RequestHeader(Headers.USER_ID) Long userId,
            @RequestParam String text,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Search text must not be blank"));
        }
        if (from == null || from < 0 || size == null || size <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid pagination"));
        }
        return itemClient.search(userId, text, from, size);
    }

    @PostMapping(path = "/{itemId}/comment", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addComment(
            @RequestHeader(Headers.USER_ID) Long userId,
            @PathVariable Long itemId,
            @RequestBody CommentCreateDto dto
    ) {
        if (dto == null || dto.getText() == null || dto.getText().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Comment text must not be blank"));
        }
        return itemClient.addComment(userId, itemId, dto);
    }

}