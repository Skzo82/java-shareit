package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

public final class ItemMapper {
    private ItemMapper() { }

    public static ItemDto toDto(Item item) {
        if (item == null) return null;
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setComments(new ArrayList<>());
        return dto;
    }

    public static ItemDto toDto(Item item, Booking last, Booking next, List<Comment> comments) {
        ItemDto dto = toDto(item);
        dto.setLastBooking(toShort(last));
        dto.setNextBooking(toShort(next));
        // комментарии маппим через CommentMapper
        dto.setComments(CommentMapper.toDtoList(comments));
        return dto;
    }

    /* ===== Booking → BookingShortDto ===== */
    private static BookingShortDto toShort(Booking b) {
        if (b == null) return null;
        return new BookingShortDto(
                b.getId(),
                b.getBooker() != null ? b.getBooker().getId() : null
        );
    }

    /* ===== ItemDto + Owner → Item ===== */
    public static Item toEntity(ItemDto dto, User owner) {
        if (dto == null) return null;
        Item item = new Item();
        item.setId(dto.getId());
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        item.setOwner(owner);
        return item;
    }

    /* ===== Patch: Item (db) <- ItemDto ===== */
    public static void merge(Item target, ItemDto patch) {
        if (patch == null || target == null) return;
        if (patch.getName() != null) target.setName(patch.getName());
        if (patch.getDescription() != null) target.setDescription(patch.getDescription());
        if (patch.getAvailable() != null) target.setAvailable(patch.getAvailable());
    }
}