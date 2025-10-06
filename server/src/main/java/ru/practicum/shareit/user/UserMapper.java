package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

public final class UserMapper {
    private UserMapper() {
    }

    public static UserDto toDto(User u) {
        if (u == null) return null;
        return new UserDto(u.getId(), u.getName(), u.getEmail());
    }

    public static User toEntity(UserDto d) {
        if (d == null) return null;
        User u = new User();
        u.setId(d.getId());
        u.setName(d.getName());
        u.setEmail(d.getEmail());
        return u;
    }

    public static void updateEntity(User target, UserDto patch) {
        if (patch.getName() != null) target.setName(patch.getName());
        if (patch.getEmail() != null) target.setEmail(patch.getEmail());
    }
}
