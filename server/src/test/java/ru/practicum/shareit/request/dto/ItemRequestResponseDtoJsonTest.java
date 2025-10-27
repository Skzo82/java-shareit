package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemShortDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestResponseDtoJsonTest {

    @Autowired
    JacksonTester<ItemRequestResponseDto> json;

    @Test
    void serialize_ok() throws Exception {
        ItemRequestResponseDto dto = ItemRequestResponseDto.builder()
                .id(5L)
                .description("Prova")
                .created(LocalDateTime.of(2025, 1, 2, 3, 4, 5))
                .items(List.of(ItemShortDto.builder().id(1L).name("Trapano").ownerId(2L).build()))
                .build();

        var doc = json.write(dto);
        assertThat(doc).extractingJsonPathNumberValue("$.id").isEqualTo(5);
        assertThat(doc).extractingJsonPathStringValue("$.description").isEqualTo("Prova");
        assertThat(doc).extractingJsonPathStringValue("$.created").contains("2025-01-02T03:04:05");
        assertThat(doc).extractingJsonPathArrayValue("$.items").hasSize(1);
        assertThat(doc).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Trapano");
    }
}