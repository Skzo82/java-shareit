package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(Long ownerId, ItemDto dto);

    ItemDto update(Long ownerId, ItemDto dto);

    ItemDto getItemById(Long viewerId, Long itemId);

    List<ItemDto> getItemsByOwner(Long ownerId, int from, int size);

    List<ItemDto> search(String text, int from, int size);

    CommentDto addComment(Long userId, Long itemId, CommentCreateDto dto);
}