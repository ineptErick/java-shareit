package ru.practicum.shareit.item.comment;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CommentGatewayDto {
    @NotBlank(message = "Отзыв не может быть пустым.")
    private String text;
}
