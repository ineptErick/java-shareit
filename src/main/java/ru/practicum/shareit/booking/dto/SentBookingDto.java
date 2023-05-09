package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.util.BookingStatus;

import java.time.LocalDateTime;

@Data
public class SentBookingDto {
    // Классы-DTO не должны содержать в себе сложные объекты по типу классов-сущностей.
    // Мы всегда должны контролировать те данные, что получаем и отдаем.
    // Не только в целях безопасности, но и производительности,
    // так как зачем тратить ресурсы на передачу тех данных, что не будут использоваться ->
    private long id;
    private String name;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private Booker booker;
    private Item item;

    // Можно было бы представить их в виде двух внутренних классов:
    // - done
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Booker {
        private long id;
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private long id;
        private String name;
    }
}
