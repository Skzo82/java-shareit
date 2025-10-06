package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ItemRequestCreateDto {
    // Текст запроса, обязателен
    @NotBlank(message = "description must not be blank")
    private String description;
}
