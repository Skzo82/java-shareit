package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestCreateDto {
    @NotBlank(message = "Описание запроса не должно быть пустым")
    private String description;
}