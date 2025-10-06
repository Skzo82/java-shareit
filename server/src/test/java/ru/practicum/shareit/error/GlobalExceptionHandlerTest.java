package ru.practicum.shareit.error;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.item.ItemDto;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class GlobalExceptionHandlerTest {


    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleMethodArgumentNotValid_shouldReturnMessageFromFieldError() throws NoSuchMethodException {
        class Dummy {
            public void m(@jakarta.validation.Valid ItemDto dto) {
            }
        }
        Method method = Dummy.class.getDeclaredMethod("m", ItemDto.class);
        MethodParameter mp = new MethodParameter(method, 0);

        ItemDto target = new ItemDto();
        BeanPropertyBindingResult br = new BeanPropertyBindingResult(target, "itemDto");
        br.addError(new FieldError("itemDto", "name", "Name is required"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(mp, br);

        Map<String, String> body = handler.handleMethodArgumentNotValid(ex);
        assertThat(body).containsEntry("error", "Name is required");
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
