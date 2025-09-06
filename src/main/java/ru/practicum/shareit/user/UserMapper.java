package ru.practicum.shareit.user;

// Маппер между User и UserDto
public final class UserMapper {
    private UserMapper() {
    }

    public static UserDto toDto(User user) {
        if (user == null) return null;
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public static User fromDto(UserDto dto) {
        if (dto == null) return null;
        return new User(dto.getId(), dto.getName(), dto.getEmail());
    }

    public static void merge(User target, UserDto patch) {
        if (patch.getName() != null) target.setName(patch.getName());
        if (patch.getEmail() != null) target.setEmail(patch.getEmail());
    }
}