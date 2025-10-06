package ru.practicum.shareit.error;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleMethodArgumentNotValid_shouldReturnMessageFromFieldError() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);

        Map<String, String> body = handler.handleMethodArgumentNotValid(ex);
        assertThat(body).containsKey("error");
        assertThat(body.get("error")).isNotBlank(); // "Validation error" nel fallback
    }

    @Test
    void handleConstraintViolation_shouldReturnMessage() {
        ConstraintViolation<?> cv = mock(ConstraintViolation.class);
        ConstraintViolationException ex =
                new ConstraintViolationException("from must be >= 0", Set.of(cv));

        Map<String, String> body = handler.handleConstraintViolation(ex);
        assertThat(body).containsEntry("error", "from must be >= 0");
    }
}
