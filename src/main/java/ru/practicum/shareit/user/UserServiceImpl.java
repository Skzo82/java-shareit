package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.ConflictException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto create(UserDto dto) {
        validateEmail(dto.getEmail());
        User toSave = UserMapper.toEntity(dto);
        try {
            return UserMapper.toDto(userRepository.save(toSave));
        } catch (DataIntegrityViolationException e) {
            // email уникальный -> 409
            throw new ConflictException("Email already exists: " + dto.getEmail());
        }
    }

    @Override
    @Transactional
    public UserDto update(Long id, UserDto patch) {
        User db = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
        if (patch.getEmail() != null) validateEmail(patch.getEmail());
        UserMapper.updateEntity(db, patch);
        try {
            return UserMapper.toDto(userRepository.save(db));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Email already exists: " + patch.getEmail());
        }
    }

    @Override
    public UserDto get(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toDto)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    private void validateEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }
    }
}