package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class InMemoryItemService implements ItemService {

    private final ItemRepository itemRepository;
    private final ru.practicum.shareit.user.UserRepository userRepository;

    @Override
    public Item create(Long ownerId, ItemDto dto) {
        // Проверяем, что владелец существует (кинет 404 при отсутствии)
        userRepository.getById(ownerId);
        Item item = ItemMapper.fromDto(dto, ownerId);
        return itemRepository.save(item);
    }

    @Override
    public Item update(Long ownerId, Long itemId, ItemDto patch) {
        Item existing = itemRepository.getById(itemId);
        if (!Objects.equals(existing.getOwnerId(), ownerId)) {
            throw new NotFoundException("Item not found for owner: " + itemId);
        }
        // частичное обновление — НЕ в маппере
        if (patch.getName() != null) existing.setName(patch.getName());
        if (patch.getDescription() != null) existing.setDescription(patch.getDescription());
        if (patch.getAvailable() != null) existing.setAvailable(patch.getAvailable());
        return itemRepository.save(existing);
    }

    @Override
    public Item getById(Long itemId) {
        return itemRepository.getById(itemId);
    }

    @Override
    public List<Item> getByOwner(Long ownerId) {
        return itemRepository.findByOwner(ownerId);
    }

    @Override
    public List<Item> search(String text) {
        if (text == null || text.isBlank()) return List.of();
        return itemRepository.searchAvailable(text);
    }
}
