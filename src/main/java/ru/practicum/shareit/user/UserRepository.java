package ru.practicum.shareit.user;

import java.util.List;

// Простой репозиторий для пользователей
public interface UserRepository {
    User save(User user);
    User getById(Long id);
    List<User> findAll();
    User findByEmailIgnoreCase(String email);
    void deleteById(Long id);
}