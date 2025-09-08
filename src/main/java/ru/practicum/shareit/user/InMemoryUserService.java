package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InMemoryUserService implements UserService {

    private final UserRepository userRepository;

    @Override
    public User create(User user) {
        validateEmail(user.getEmail());
        ensureEmailUnique(user.getEmail(), null);
        return userRepository.save(user);
    }

    @Override
    public User getById(Long id) {
        return userRepository.getById(id);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(Long id, UserDto patch) {
        User existing = userRepository.getById(id);
        if (patch.getEmail() != null) {
            validateEmail(patch.getEmail());
            if (!patch.getEmail().equals(existing.getEmail())) {
                ensureEmailUnique(patch.getEmail(), id);
            }
        }
        if (patch.getName() != null) existing.setName(patch.getName());
        if (patch.getEmail() != null) existing.setEmail(patch.getEmail());
        return userRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    private void validateEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new BadRequestException("Email is invalid");
        }
    }

    private void ensureEmailUnique(String email, Long selfId) {
        User found = userRepository.findByEmailIgnoreCase(email);
        if (found != null && (selfId == null || !found.getId().equals(selfId))) {
            throw new ConflictException("Email already exists: " + email);
        }
    }
}
