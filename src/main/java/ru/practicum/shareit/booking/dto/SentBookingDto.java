package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.BookingStatus;

import java.time.LocalDateTime;

@Data
public class SentBookingDto {
    private long id;
    private String name;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private Booker booker;
    private Item item;

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
