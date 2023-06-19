package ru.practicum.shareit.item.dto;

import java.time.LocalDateTime;
import lombok.Data;
import ru.practicum.shareit.util.BookingStatus;

@Data
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private Long bookerId;


}
