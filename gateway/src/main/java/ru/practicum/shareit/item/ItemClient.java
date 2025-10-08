package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

@Slf4j
@Component
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit.server.url}") String serverUrl,
                      RestTemplateBuilder builder) {
        super(builder.build(), serverUrl);
    }

    public ResponseEntity<Object> create(Long userId, ItemCreateDto dto) {
        return post(API_PREFIX, userId, dto);
    }

    public ResponseEntity<Object> getById(Long userId, Long itemId) {
        return get(API_PREFIX + "/" + itemId, userId);
    }

    public ResponseEntity<Object> getOwnerItems(Long userId, int from, int size) {
        String path = API_PREFIX + "?from=" + from + "&size=" + size;
        return get(path, userId);
    }

    public ResponseEntity<Object> getAll(Long userId, int from, int size) {
        return getOwnerItems(userId, from, size);
    }

    public ResponseEntity<Object> search(Long userId, String text, int from, int size) {
        String path = API_PREFIX + "/search?text=" + text + "&from=" + from + "&size=" + size;
        return get(path, userId);
    }

    public ResponseEntity<Object> update(Long userId, Long itemId, ItemUpdateDto dto) {
        return patch(API_PREFIX + "/" + itemId, userId, dto);
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentCreateDto dto) {
        return post(API_PREFIX + "/" + itemId + "/comment", userId, dto);
    }
}