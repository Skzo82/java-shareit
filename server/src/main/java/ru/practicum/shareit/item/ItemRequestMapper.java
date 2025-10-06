package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    // Преобразование ItemRequest → ItemRequestResponseDto
    public static ItemRequestResponseDto toResponseDto(ItemRequest request, List<Item> items) {
        ItemRequestResponseDto dto = new ItemRequestResponseDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        dto.setItems(items.stream()
                .map(ItemMapper::toShortDto)
                .collect(Collectors.toList()));
        return dto;
    }
}