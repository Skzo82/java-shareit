package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

// Сервисный интерфейс для вещей
public interface ItemService {
    Item create(Long ownerId, ItemDto dto);
    Item update(Long ownerId, Long itemId, ItemDto patch);
    Item getById(Long itemId);
    List<Item> getByOwner(Long ownerId);
    List<Item> search(String text);
}