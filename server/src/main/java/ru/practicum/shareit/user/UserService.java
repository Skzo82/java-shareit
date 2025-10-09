package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

public interface UserService {

    UserDto create(UserDto dto);

    UserDto update(long userId, UserUpdateDto dto);

    UserDto getById(long userId);

    List<UserDto> getAll(int from, int size);

    void delete(long userId);
}