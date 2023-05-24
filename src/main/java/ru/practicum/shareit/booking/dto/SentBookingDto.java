package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.util.BookingStatus;

import java.time.LocalDateTime;

@Data
public class SentBookingDto {
    private  Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private Booker booker;
    private Item item;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Booker extends ru.practicum.shareit.user.model.User {
        private Long id;
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item extends ru.practicum.shareit.item.model.Item {
        private Long id;
        private String name;
    }
}
