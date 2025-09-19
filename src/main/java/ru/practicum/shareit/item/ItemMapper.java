package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

public final class ItemMapper {

    private ItemMapper() {
    }

    // // маппинг Item -> ItemDto (без бронирований и комментариев)
    public static ItemDto toDto(Item item) {
        if (item == null) return null;

        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        return dto;
    }

    // // маппинг Item -> ItemDto с бронированиями и комментариями
    public static ItemDto toDto(Item item,
                                Booking lastBooking,
                                Booking nextBooking,
                                List<Comment> comments) {
        ItemDto dto = toDto(item);
        dto.setLastBooking(BookingMapper.toShort(lastBooking));
        dto.setNextBooking(BookingMapper.toShort(nextBooking));
        dto.setComments(toCommentDtoList(comments));
        return dto;
    }

    // // маппинг Comment -> CommentDto
    public static CommentDto toCommentDto(Comment comment) {
        if (comment == null) return null;

        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    // // маппинг списка Comment -> список CommentDto
    public static List<CommentDto> toCommentDtoList(List<Comment> comments) {
        if (comments == null) return List.of();
        return comments.stream()
                .map(ItemMapper::toCommentDto)
                .collect(Collectors.toList());
    }
}
