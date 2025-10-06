package ru.practicum.shareit.item.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemUpdateDto {
    // Все поля опциональные для PATCH
    private String name;
    private String description;
    private Boolean available;
    private Long requestId; // можно изменить/сбросить привязку (если бизнес-логика позволяет)
}
