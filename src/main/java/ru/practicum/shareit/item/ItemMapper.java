package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

// Маппер между Item и ItemDto
public final class ItemMapper {
    private ItemMapper() {
    }

    public static ItemDto toDto(Item item) {
        if (item == null) return null;
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable());
    }

    public static Item fromDto(ItemDto dto, Long ownerId) {
        if (dto == null) return null;
        return new Item(dto.getId(), dto.getName(), dto.getDescription(), dto.getAvailable(), ownerId);
    }
}