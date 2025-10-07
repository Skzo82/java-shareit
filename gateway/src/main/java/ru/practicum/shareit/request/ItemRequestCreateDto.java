package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestCreateDto {

    // Текст запроса на вещь
    @NotBlank(message = "Описание запроса не должно быть пустым")
    private String description;
}