package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.user.dto.UserBookingDto;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private UserBookingDto booker;
    private String status;
    private ItemBookingDto item;


    public BookingDto(Long bookingId, LocalDateTime start, LocalDateTime end, String status, Long bookerId,
                      Long itemId, String itemName) {
        this.id = bookingId;
        this.start = start;
        this.end = end;
        this.booker = new UserBookingDto(bookerId);
        this.status = status;
        this.item = new ItemBookingDto(itemId, itemName);
    }
}