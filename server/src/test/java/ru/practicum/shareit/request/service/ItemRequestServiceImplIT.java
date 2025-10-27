package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.ANY)
@Sql(scripts = {"/schema.sql", "/data.sql"})
class ItemRequestServiceImplIT {

    @Autowired
    private ItemRequestService service;

    @Test
    @DisplayName("create(): сохраняет запрос и возвращает DTO без items")
    void create_shouldPersistAndReturnDto() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Нужна лобзиковая пила");

        ItemRequestResponseDto saved = service.create(1L, dto);

        assertNotNull(saved.getId());
        assertEquals("Нужна лобзиковая пила", saved.getDescription());
        assertNotNull(saved.getCreated());
        assertNotNull(saved.getItems());
        assertTrue(saved.getItems().isEmpty());
    }

    @Test
    @DisplayName("create(): NotFoundException, если пользователь не существует")
    void create_shouldThrowWhenUserNotFound() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Проверка");
        assertThrows(NotFoundException.class, () -> service.create(999L, dto));
    }

    @Test
    @DisplayName("getOwn(): возвращает запросы пользователя по убыванию created с прикреплёнными items")
    void getOwn_shouldReturnSortedWithItems() {
        List<ItemRequestResponseDto> list = service.getOwn(2L);

        assertEquals(1, list.size());
        ItemRequestResponseDto r = list.get(0);
        assertEquals(2L, r.getId());
        assertNotNull(r.getCreated());
        assertNotNull(r.getItems());
        assertEquals(1, r.getItems().size());
        assertEquals(2L, r.getItems().get(0).getId());
    }

    @Test
    @DisplayName("getAll(): esclude le proprie richieste e rispetta ordinamento/paginazione")
    void getAll_shouldExcludeOwnAndPaginate() {
        List<ItemRequestResponseDto> all = service.getAll(1L, 0, 10);
        assertEquals(2, all.size());
        assertEquals(1L, all.get(0).getId());
        assertEquals(2L, all.get(1).getId());

        List<ItemRequestResponseDto> p1 = service.getAll(1L, 0, 1);
        assertEquals(1, p1.size());
        assertEquals(1L, p1.get(0).getId());

        List<ItemRequestResponseDto> p2 = service.getAll(1L, 1, 1);
        assertEquals(1, p2.size());
        assertEquals(2L, p2.get(0).getId());

        List<ItemRequestResponseDto> p3 = service.getAll(1L, 2, 1);
        assertTrue(p3.isEmpty());
    }


    @Test
    @DisplayName("getAll(): NotFoundException, если пользователь не существует")
    void getAll_shouldThrowWhenUserNotFound() {
        assertThrows(NotFoundException.class, () -> service.getAll(999L, 0, 5));
    }

    @Test
    @DisplayName("getById(): возвращает конкретный запрос с прикреплёнными items")
    void getById_shouldReturnRequestWithItems() {
        ItemRequestResponseDto dto = service.getById(1L, 1L);

        assertEquals(1L, dto.getId());
        assertEquals("Ищу дрель на выходные", dto.getDescription());
        assertNotNull(dto.getCreated());
        assertNotNull(dto.getItems());
        assertEquals(1, dto.getItems().size());
        assertEquals(1L, dto.getItems().get(0).getId());
        assertEquals("Дрель", dto.getItems().get(0).getName());
    }

    @Test
    @DisplayName("getById(): NotFoundException при несуществующем пользователе или запросе")
    void getById_shouldThrowWhenUserOrRequestNotFound() {
        assertThrows(NotFoundException.class, () -> service.getById(999L, 1L));
        assertThrows(NotFoundException.class, () -> service.getById(1L, 999L));
    }
}
