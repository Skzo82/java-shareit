package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequestDto {

    @NotNull(message = "itemId обязателен")
    private Long itemId;

    @NotNull(message = "start обязателен")
    @FutureOrPresent(message = "start не может быть в прошлом")
    private LocalDateTime start;

    @NotNull(message = "end обязателен")
    @Future(message = "end должен быть в будущем")
    private LocalDateTime end;

    @AssertTrue(message = "end должен быть позже start")
    public boolean isTimeRangeValid() {
        return start != null && end != null && end.isAfter(start);
    }
}