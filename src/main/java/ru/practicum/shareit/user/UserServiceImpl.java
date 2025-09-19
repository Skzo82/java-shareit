package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.*;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public User create(User user) {
        validateEmail(user.getEmail());
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Email already used");
        }
    }

    @Override
    @Transactional
    public User update(User user) {
        User db = userRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (user.getName() != null) db.setName(user.getName());
        if (user.getEmail() != null) {
            validateEmail(user.getEmail());
            db.setEmail(user.getEmail());
        }
        try {
            return userRepository.save(db);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("email conflict");
        }
    }

    @Override
    public User get(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    private void validateEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new ValidationException("invalid email");
        }
    }
}
