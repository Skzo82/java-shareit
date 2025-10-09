package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @deprecated Используйте BookingRequestDto.
 * Класс оставлен для совместимости с тестами.
 */
@Deprecated
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookItemRequestDto {
    @NotNull(message = "itemId обязателен")
    private Long itemId;

    @FutureOrPresent(message = "start не может быть в прошлом")
    private LocalDateTime start;

    @NotNull(message = "end обязателен")
    @Future(message = "end должен быть в будущем")
    private LocalDateTime end;
}
