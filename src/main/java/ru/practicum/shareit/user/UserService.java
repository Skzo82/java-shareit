package ru.practicum.shareit.user;

import java.util.List;

// Сервисный интерфейс для пользователей
public interface UserService {
    User create(User user);

    User getById(Long id);

    List<User> getAll();

    User update(Long id, UserDto patch);

    void delete(Long id);
}