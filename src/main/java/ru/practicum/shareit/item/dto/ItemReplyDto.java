package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.model.BookingDate;
import ru.practicum.shareit.item.model.Comment;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemReplyDto extends ItemCreationDto {
    private Long requestId;

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingDate lastBooking;
    private BookingDate nextBooking;

    private Set<Comment> comments;
}
