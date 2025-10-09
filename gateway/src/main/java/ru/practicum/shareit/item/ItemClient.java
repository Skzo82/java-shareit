package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

@Component
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit.server.url}") String serverUrl,
                      RestTemplate restTemplate) {
        super(restTemplate, serverUrl);
    }

    public ResponseEntity<Object> create(Long userId, ItemCreateDto dto) {
        return post(API_PREFIX, userId, dto);
    }

    public ResponseEntity<Object> getById(Long userId, Long itemId) {
        return get(API_PREFIX + "/" + itemId, userId);
    }

    public ResponseEntity<Object> getOwnerItems(Long userId, int from, int size) {
        return get(API_PREFIX + "?from=" + from + "&size=" + size, userId);
    }

    public ResponseEntity<Object> getAll(Long userId, int from, int size) {
        return getOwnerItems(userId, from, size);
    }

    public ResponseEntity<Object> search(Long userId, String text, int from, int size) {
        return get(API_PREFIX + "/search?text=" + text + "&from=" + from + "&size=" + size, userId);
    }

    public ResponseEntity<Object> update(Long userId, Long itemId, ItemUpdateDto dto) {
        return patch(API_PREFIX + "/" + itemId, userId, dto);
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentCreateDto dto) {
        return post(API_PREFIX + "/" + itemId + "/comment", userId, dto);
    }
}