package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class ItemRequestController {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    private final ItemRequestService service;

    @PostMapping
    public ItemRequestResponseDto create(@RequestHeader(USER_HEADER) Long userId,
                                         @RequestBody ItemRequestCreateDto dto) {
        // Валидация уже в gateway
        return service.create(userId, dto);
    }

    @GetMapping
    public List<ItemRequestResponseDto> getOwn(@RequestHeader(USER_HEADER) Long userId) {
        return service.getOwn(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAll(@RequestHeader(USER_HEADER) Long userId,
                                               @RequestParam int from,
                                               @RequestParam int size) {
        return service.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getById(@RequestHeader(USER_HEADER) Long userId,
                                          @PathVariable Long requestId) {
        return service.getById(userId, requestId);
    }
}
