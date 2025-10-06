package ru.practicum.shareit.item;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ItemDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldFailWhenNameBlank() {
        ItemDto dto = new ItemDto();
        dto.setName("   ");
        dto.setDescription("desc");
        dto.setAvailable(true);

        var violations = validator.validate(dto);
        assertThat(violations)
                .anySatisfy(v -> assertThat(v.getMessage()).contains("Name is required"));
    }

    @Test
    void shouldFailWhenDescriptionBlank() {
        ItemDto dto = new ItemDto();
        dto.setName("Дрель");
        dto.setDescription("  ");
        dto.setAvailable(true);

        var violations = validator.validate(dto);
        assertThat(violations)
                .anySatisfy(v -> assertThat(v.getMessage()).contains("Description is required"));
    }

    @Test
    void shouldFailWhenAvailableNull() {
        ItemDto dto = new ItemDto();
        dto.setName("Дрель");
        dto.setDescription("OK");
        dto.setAvailable(null);

        var violations = validator.validate(dto);
        assertThat(violations)
                .anySatisfy(v -> assertThat(v.getMessage()).contains("Available flag is required"));
    }

    @Test
    void shouldPassWhenValid() {
        ItemDto dto = new ItemDto();
        dto.setName("Дрель");
        dto.setDescription("OK");
        dto.setAvailable(true);

        var violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }
}
