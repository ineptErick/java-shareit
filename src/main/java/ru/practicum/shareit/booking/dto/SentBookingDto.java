package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.util.BookingStatus;

import java.time.LocalDateTime;

@Data
public class SentBookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private User booker;
    private Item item;

    // комментарий: зачем мы тут наследуемся от моделей? Это вспомогательные дто
    // ответ: сейчас удалила наследование и отрефакторила

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User {
        private Long id;
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private Long id;
        private String name;
    }
}
