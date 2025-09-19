package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    User create(User user);

    User update(User user);

    User get(Long id);

    List<User> getAll();

    void delete(Long id);
}
