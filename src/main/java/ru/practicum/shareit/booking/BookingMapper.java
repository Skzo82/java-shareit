package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.ItemShortDto;
import ru.practicum.shareit.booking.dto.UserShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.BookingShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

public final class BookingMapper {
    private BookingMapper() {
    }

    // // маппинг из запроса в сущность
    public static Booking toEntity(BookingRequestDto dto, Item item, User booker) {
        Booking b = new Booking();
        b.setStart(dto.getStart());
        b.setEnd(dto.getEnd());
        b.setItem(item);
        b.setBooker(booker);
        b.setStatus(BookingStatus.WAITING);
        return b;
    }

    // // маппинг сущности в ответ
    public static BookingResponseDto toDto(Booking b) {
        return new BookingResponseDto(
                b.getId(),
                b.getStart(),
                b.getEnd(),
                b.getStatus(),
                new UserShortDto(b.getBooker().getId()),
                new ItemShortDto(b.getItem().getId(), b.getItem().getName())
        );
    }

    public static BookingShortDto toShort(Booking b) {
        return b == null ? null : new BookingShortDto(b.getId(), b.getBooker().getId());
    }

}
