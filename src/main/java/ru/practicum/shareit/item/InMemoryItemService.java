package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

// Простая in-memory реализация сервиса вещей
@Service
public class InMemoryItemService implements ItemService {

    private final Map<Long, Item> storage = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);
    private final UserService userService;

    public InMemoryItemService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Item create(Long ownerId, ItemDto dto) {
        if (ownerId == null) throw new BadRequestException("X-Sharer-User-Id header is required");
        userService.getById(ownerId);

        if (dto.getAvailable() == null) throw new BadRequestException("Field 'available' is required");
        Item item = ItemMapper.fromDto(dto, ownerId);
        item.setId(seq.incrementAndGet());
        storage.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Long ownerId, Long itemId, ItemDto patch) {
        Item existing = getById(itemId);
        if (!Objects.equals(existing.getOwnerId(), ownerId))
            throw new NotFoundException("Item not found for owner: " + itemId);
        ItemMapper.merge(existing, patch);
        return existing;
    }

    @Override
    public Item getById(Long itemId) {
        Item it = storage.get(itemId);
        if (it == null) throw new NotFoundException("Item not found: " + itemId);
        return it;
    }

    @Override
    public List<Item> getByOwner(Long ownerId) {
        return storage.values().stream()
                .filter(i -> Objects.equals(i.getOwnerId(), ownerId))
                .sorted(Comparator.comparing(Item::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String text) {
        if (text == null || text.isBlank()) return List.of();
        String q = text.toLowerCase();
        return storage.values().stream()
                .filter(Item::getAvailable)
                .filter(i ->
                        (i.getName() != null && i.getName().toLowerCase().contains(q)) ||
                                (i.getDescription() != null && i.getDescription().toLowerCase().contains(q))
                )
                .sorted(Comparator.comparing(Item::getId))
                .collect(Collectors.toList());
    }
}