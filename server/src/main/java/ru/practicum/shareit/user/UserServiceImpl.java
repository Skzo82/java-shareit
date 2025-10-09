package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository; // o come si chiama
    private final UserMapper mapper;             // se usi mapper

    @Override
    public UserDto create(UserDto dto) {
        User entity = mapper.toEntity(dto);
        return mapper.toDto(userRepository.save(entity));
    }

    @Override
    public UserDto update(long userId, UserUpdateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        return mapper.toDto(userRepository.save(user));
    }

    @Override
    public UserDto getById(long userId) {
        return userRepository.findById(userId)
                .map(mapper::toDto)
                .orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
    }

    @Override
    public List<UserDto> getAll(int from, int size) {
        int page = from / size;
        return userRepository.findAll(PageRequest.of(page, size))
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(long userId) {
        userRepository.deleteById(userId);
    }
}
