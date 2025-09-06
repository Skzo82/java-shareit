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

    public static void merge(Item target, ItemDto patch) {
        if (patch.getName() != null) target.setName(patch.getName());
        if (patch.getDescription() != null) target.setDescription(patch.getDescription());
        if (patch.getAvailable() != null) target.setAvailable(patch.getAvailable());
    }
}