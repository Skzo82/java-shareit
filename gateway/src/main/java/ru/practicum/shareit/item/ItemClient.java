package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Slf4j
@Component
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";
    @SuppressWarnings("unused")
    private final RestTemplate rest;

    @Autowired
    public ItemClient(@Value("${shareit.server.url}") String serverUrl,
                      RestTemplateBuilder builder) {
        super(
                builder
                        .rootUri(serverUrl)
                        .requestFactory(settings -> new HttpComponentsClientHttpRequestFactory())
                        .setConnectTimeout(Duration.ofSeconds(5))
                        .setReadTimeout(Duration.ofSeconds(30))
                        .errorHandler(new DefaultResponseErrorHandler() {
                            @Override
                            public boolean hasError(ClientHttpResponse response) throws IOException {
                                return false;
                            }
                        })
                        .build(),
                serverUrl
        );
        this.rest = builder.build();
    }

    public ItemClient(RestTemplate restTemplate) {
        super(restTemplate, "http://localhost:9090");
        this.rest = restTemplate;
    }

    public ResponseEntity<Object> create(Long userId, ItemCreateDto dto) {
        return post(API_PREFIX, userId, dto);
    }

    public ResponseEntity<Object> update(Long userId, Long itemId, ItemUpdateDto dto) {
        return patch(API_PREFIX + "/" + itemId, userId, dto);
    }

    public ResponseEntity<Object> getById(Long userId, Long itemId) {
        return get(API_PREFIX + "/" + itemId, userId);
    }

    public ResponseEntity<Object> getOwnerItems(Long userId, int from, int size) {
        return get(API_PREFIX + "?from=" + from + "&size=" + size, userId);
    }

    public ResponseEntity<Object> search(Long userId, String text, int from, int size) {
        return get(API_PREFIX + "/search?text=" + UriUtils.encode(text, StandardCharsets.UTF_8)
                + "&from=" + from + "&size=" + size, userId);
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentCreateDto dto) {
        return post(API_PREFIX + "/" + itemId + "/comment", userId, dto);
    }
}