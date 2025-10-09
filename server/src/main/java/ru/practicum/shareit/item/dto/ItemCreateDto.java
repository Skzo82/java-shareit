package ru.practicum.shareit.item.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemCreateDto {
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}