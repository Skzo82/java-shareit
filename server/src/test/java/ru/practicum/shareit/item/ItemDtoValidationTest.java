package ru.practicum.shareit.item;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ItemDtoValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    void shouldFailWhenNameBlank() {
        ItemDto dto = new ItemDto();
        dto.setName("   ");
        dto.setDescription("desc");
        dto.setAvailable(true);

        Set<?> violations = validator.validate(dto);
        assertThat(violations).anySatisfy(v ->
                assertThat(v.toString()).contains("Name is required"));
    }

    @Test
    void shouldFailWhenDescriptionBlank() {
        ItemDto dto = new ItemDto();
        dto.setName("Drill");
        dto.setDescription("  ");
        dto.setAvailable(true);

        Set<?> violations = validator.validate(dto);
        assertThat(violations).anySatisfy(v ->
                assertThat(v.toString()).contains("Description is required"));
    }

    @Test
    void shouldFailWhenAvailableNull() {
        ItemDto dto = new ItemDto();
        dto.setName("Drill");
        dto.setDescription("desc");
        dto.setAvailable(null);

        Set<?> violations = validator.validate(dto);
        assertThat(violations).anySatisfy(v ->
                assertThat(v.toString()).contains("Available flag is required"));
    }

    @Test
    void shouldPassWhenAllFieldsValid() {
        ItemDto dto = new ItemDto();
        dto.setName("Drill");
        dto.setDescription("desc");
        dto.setAvailable(true);

        assertThat(validator.validate(dto)).isEmpty();
    }
}
