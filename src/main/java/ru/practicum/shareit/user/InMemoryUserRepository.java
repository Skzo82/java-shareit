package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> storage = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    @Override
    public User save(User user) {
        if (user.getId() == null) user.setId(seq.incrementAndGet());
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
    public List<User> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public User findByEmailIgnoreCase(String email) {
        if (email == null) return null;
        return storage.values().stream()
                .filter(u -> email.equalsIgnoreCase(u.getEmail()))
                .findFirst().orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }
}