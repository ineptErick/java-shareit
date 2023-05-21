package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    // Тут не совсем верно, внутренние классы не требуются)
    // Я лишь предложил объявить два отдельных классах, Один для принимаемого значения, а другой - для возвращаемого)
    // - done
    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
