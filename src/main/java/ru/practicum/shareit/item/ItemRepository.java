package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;
import java.util.List;

// Простой репозиторий для хранения вещей в памяти
public interface ItemRepository {
    Item save(Item item);
    Item getById(Long id);
    List<Item> findByOwner(Long ownerId);
    List<Item> searchAvailable(String query);
}