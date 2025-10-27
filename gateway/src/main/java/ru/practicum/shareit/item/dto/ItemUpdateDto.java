package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemUpdateDto {

    @Size(max = 255, message = "name: максимум 255 символов")
    private String name;

    @Size(max = 1000, message = "description: максимум 1000 символов")
    private String description;

    private Boolean available;

    private Long requestId;
}