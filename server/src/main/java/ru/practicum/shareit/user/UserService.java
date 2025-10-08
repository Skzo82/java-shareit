package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto dto);

    UserDto updatePartial(Long id, UserUpdateDto patch);

    UserDto get(Long id);

    List<UserDto> getAll();

    void delete(Long id);
}