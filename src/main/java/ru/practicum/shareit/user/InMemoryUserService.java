package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class InMemoryUserService implements UserService {
    private final Map<Long, User> storage = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    @Override
    public User create(User user) {
        validateEmail(user.getEmail());
        ensureEmailUnique(user.getEmail(), null);
        user.setId(seq.incrementAndGet());
        storage.put(user.getId(), user);
        return user;
    }

    @Override
    public User getById(Long id) {
        User u = storage.get(id);
        if (u == null) throw new NotFoundException("User not found: " + id);
        return u;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public User update(Long id, UserDto patch) {
        User existing = getById(id);
        if (patch.getEmail() != null) {
            validateEmail(patch.getEmail());
            // Разрешаем ту же самую почту у самого пользователя
            if (!patch.getEmail().equals(existing.getEmail())) {
                ensureEmailUnique(patch.getEmail(), id);
            }
        }
        UserMapper.merge(existing, patch);
        return existing;
    }

    @Override
    public void delete(Long id) {
        storage.remove(id);
    }

    private void validateEmail(String email) {
        if (email == null || !email.contains("@"))
            throw new BadRequestException("Email is invalid");
    }

    private void ensureEmailUnique(String email, Long selfId) {
        boolean exists = storage.values().stream()
                .anyMatch(u -> u.getEmail() != null
                        && u.getEmail().equalsIgnoreCase(email)
                        && (selfId == null || !u.getId().equals(selfId)));
        if (exists) throw new ConflictException("Email already exists: " + email);
    }
}