package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public final class CommentMapper {
    private CommentMapper() {
    }

    public static Comment toEntityFromCreate(CommentCreateDto dto, Item item, User author, LocalDateTime created) {
        Comment c = new Comment();
        c.setText(dto.getText());
        c.setItem(item);
        c.setAuthor(author);
        c.setCreated(created);
        return c;
    }

    public static CommentDto toDto(Comment c) {
        return new CommentDto(
                c.getId(),
                c.getText(),
                c.getAuthor().getName(),
                c.getCreated()
        );
    }
}