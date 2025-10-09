package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody UserDto dto) {
        return ResponseEntity.ok(userService.create(dto));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<?> update(@PathVariable long userId,
                                    @RequestBody UserUpdateDto dto) {
        return ResponseEntity.ok(userService.update(userId, dto));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getById(@PathVariable long userId) {
        return ResponseEntity.ok(userService.getById(userId));
    }

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(defaultValue = "0") int from,
                                    @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(userService.getAll(from, size));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> delete(@PathVariable long userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }
}
