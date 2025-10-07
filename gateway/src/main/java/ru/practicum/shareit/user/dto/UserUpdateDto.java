package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateDto {

    // Разрешаем пустое значение (означает "не обновлять").
    private String name;

    @Email(message = "Некорректный формат email")
    private String email;
}