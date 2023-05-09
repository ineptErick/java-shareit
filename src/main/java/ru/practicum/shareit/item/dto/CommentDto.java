package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class CommentDto {

    private Long id;

    // Также можно было бы использовать @Size, чтобы ограничить размер текстового поля на основе тех ограничений,
    // что установлены на уровне таблицы БД
    // - done
    @Size(max = 250)
    // Хорошо было бы над полем text навесить аннотацию @NotBlank,
    // так как значение не должно быть null и строка не может быть пустой и состоящей из пробелов
    // - done
    @NotBlank
    private String text;

    private String authorName;

    private LocalDateTime created;

    private RequestBody requestBody;
    private AnswerBody answerBody;

    // Также хорошо было бы разбить класс на несколько, чтобы один использовался для представлена тела запроса,
    // а другой - для представления тела ответа
    // - done
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestBody {
        private Long id;

        @Size(max = 250)
        @NotBlank
        private String text;

        private String authorName;

        private LocalDateTime created;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerBody {
        private Long id;

        @Size(max = 250)
        @NotBlank
        private String text;

        private String authorName;

        private LocalDateTime created;
    }
}
