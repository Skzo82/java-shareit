package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    private final ItemService itemService;

    // CREATE
    @PostMapping
    public ResponseEntity<ItemDto> create(@RequestHeader(USER_HEADER) Long ownerId,
                                          @RequestBody ItemDto dto) {
        return ResponseEntity.ok(itemService.create(ownerId, dto));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getById(@RequestHeader(USER_HEADER) Long viewerId,
                                           @PathVariable Long itemId) {
        return ResponseEntity.ok(itemService.getItemById(viewerId, itemId));
    }

    // GET OWNER ITEMS
    @GetMapping
    public ResponseEntity<List<ItemDto>> getOwnerItems(@RequestHeader(USER_HEADER) Long ownerId,
                                                       @RequestParam(defaultValue = "0") Integer from,
                                                       @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(itemService.getItemsByOwner(ownerId, from, size));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> search(@RequestParam String text,
                                                @RequestParam(defaultValue = "0") Integer from,
                                                @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(itemService.search(text, from, size));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(@RequestHeader(USER_HEADER) Long ownerId,
                                          @PathVariable Long itemId,
                                          @RequestBody ItemDto dto) {
        dto.setId(itemId);
        return ResponseEntity.ok(itemService.update(ownerId, dto));
    }

    // ADD COMMENT
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@RequestHeader(USER_HEADER) Long userId,
                                                 @PathVariable Long itemId,
                                                 @RequestBody CommentCreateDto dto) {
        return ResponseEntity.ok(itemService.addComment(userId, itemId, dto));
    }
}