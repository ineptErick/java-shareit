package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.item.comment.CommentDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private LocalDateTime lastBookingDate;
    private LocalDateTime nextBookingDate;
    private BookingInfoDto lastBooking;
    private BookingInfoDto nextBooking;
    private Long requestId;
    private List<CommentDto> comments = new ArrayList<>();
}
