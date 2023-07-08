package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.user.dto.UserBookingDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    @Test
    void toBookingDto() {
        Booking booking = new Booking();
        ItemBookingDto item = new ItemBookingDto();
        UserBookingDto user = new UserBookingDto();

        booking.setId(0L);
        booking.setItemId(1L);
        booking.setBookerId(2L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(BookingStatus.WAITING.toString());

        item.setId(1L);
        item.setName("Item");

        user.setId(2L);

        BookingDto bookingDto = BookingMapper.INSTANT.toBookingDto(booking, item, user);

        Assertions.assertAll(
                () -> assertEquals(bookingDto.getId(), booking.getId()),
                () -> assertEquals(bookingDto.getEnd(), booking.getEnd()),
                () -> assertEquals(bookingDto.getStart(), booking.getStart()),
                () -> assertEquals(bookingDto.getStatus(), booking.getStatus()),
                () -> assertEquals(bookingDto.getBooker().getId(), user.getId()),
                () -> assertEquals(bookingDto.getItem().getId(), item.getId()),
                () -> assertEquals(bookingDto.getItem().getName(), item.getName())
        );

    }
}