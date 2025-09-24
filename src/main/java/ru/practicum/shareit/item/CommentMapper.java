package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * // Маппер для сущности Comment: преобразование между Entity и DTO
 */
public final class CommentMapper {

    private CommentMapper() {
    }

    // Создание Comment из DTO запроса + связанной Item и User
    public static Comment toEntityFromCreate(CommentCreateDto dto, Item item, User author, LocalDateTime created) {
        Comment c = new Comment();
        c.setText(dto.getText());
        c.setItem(item);
        c.setAuthor(author);
        c.setCreated(created);
        return c;
    }

    // Преобразование Comment → CommentDto
    public static CommentDto toDto(Comment c) {
        return new CommentDto(
                c.getId(),
                c.getText(),
                c.getAuthor().getName(),
                c.getCreated()
        );
    }

    // Преобразование списка Comment → список CommentDto
    public static List<CommentDto> toDtoList(List<Comment> list) {
        if (list == null || list.isEmpty()) return new ArrayList<>();
        List<CommentDto> out = new ArrayList<>(list.size());
        for (Comment c : list) {
            out.add(toDto(c));
        }
        return out;
    }
}