package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentCreateDto {
    @NotBlank(message = "Comment text must not be blank")
    private String text;
}