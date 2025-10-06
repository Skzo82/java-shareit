package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemCreateDto {
    // Название вещи
    @NotBlank(message = "name must not be blank")
    private String name;

    // Описание
    @NotBlank(message = "description must not be blank")
    private String description;

    // Доступность (обязательна)
    @NotNull(message = "available must not be null")
    private Boolean available;

    // Опциональная привязка к запросу
    private Long requestId;
}
