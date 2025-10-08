package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.BadRequestException;
import ru.practicum.shareit.error.ConflictException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto create(UserDto dto) {
        if (dto.getEmail() == null) {
            throw new BadRequestException("Email обязателен");
        }
        try {
            User saved = userRepository.save(UserMapper.toEntity(dto));
            return UserMapper.toDto(saved);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Email already exists: " + dto.getEmail());
        }
    }

    @Override
    @Transactional
    public UserDto updatePartial(Long id, UserUpdateDto patch) {
        User db = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));

        if (patch.getName() != null && !patch.getName().isBlank()) {
            db.setName(patch.getName());
        }
        if (patch.getEmail() != null && !patch.getEmail().isBlank()) {
            validateEmailFormatOr400(patch.getEmail());
            userRepository.findByEmail(patch.getEmail())
                    .filter(u -> !u.getId().equals(id))
                    .ifPresent(u -> {
                        throw new ConflictException("Email already exists: " + patch.getEmail());
                    });

            db.setEmail(patch.getEmail());
        }

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

    private void validateEmailFormatOr400(String email) {
        if (email == null || !email.contains("@")) {
            throw new BadRequestException("Некорректный формат email");
        }
    }
}