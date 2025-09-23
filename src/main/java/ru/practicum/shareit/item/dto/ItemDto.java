package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;

    @NotBlank(message = "Name is required") // имя обязательно
    private String name;

    private String description; // описание может быть пустым

    @NotNull(message = "Available flag is required") // доступность обязательна
    private Boolean available;

    private BookingShortDto lastBooking; // показ для владельца
    private BookingShortDto nextBooking; // показ для владельца
    private List<CommentDto> comments = new ArrayList<>();
}