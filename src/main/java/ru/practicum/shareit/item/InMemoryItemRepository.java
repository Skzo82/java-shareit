package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

// Хранение вещей в памяти
@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> storage = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    @Override
    public Item save(Item item) {
        if (item.getId() == null) item.setId(seq.incrementAndGet());
        storage.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getById(Long id) {
        Item it = storage.get(id);
        if (it == null) throw new NotFoundException("Item not found: " + id);
        return it;
    }

    @Override
    public List<Item> findByOwner(Long ownerId) {
        return storage.values().stream()
                .filter(i -> Objects.equals(i.getOwnerId(), ownerId))
                .sorted(Comparator.comparing(Item::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchAvailable(String q) {
        String query = q.toLowerCase();
        return storage.values().stream()
                .filter(Item::getAvailable)
                .filter(i ->
                        (i.getName() != null && i.getName().toLowerCase().contains(query)) ||
                                (i.getDescription() != null && i.getDescription().toLowerCase().contains(query))
                )
                .sorted(Comparator.comparing(Item::getId))
                .collect(Collectors.toList());
    }
}
