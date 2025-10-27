package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.ANY)
@Sql(scripts = {"/schema.sql", "/data.sql"})
class ItemServiceImplIT {

    @Autowired
    private ItemService itemService;

    @Test
    @DisplayName("getItemsByOwner(): возвращает вещи владельца с пагинацией")
    void getItemsByOwner_ok() {
        List<ItemDto> list = itemService.getItemsByOwner(1L, 0, 10);
        assertFalse(list.isEmpty());
        assertTrue(list.stream().anyMatch(i -> i.getName().contains("Дрель")));
    }
}