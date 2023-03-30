package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.BookingDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.xml.stream.events.Comment;
import java.util.Set;

/**
 * TODO Sprint add-controllers.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class ItemDto {
    int id;
    int owner;

    @NotBlank
    String name;
    @NotBlank
    String description;
    @NotNull
    Boolean available;
    private BookingDate lastBooking;
    private BookingDate nextBooking;
    private Set<Comment> comments;
}
