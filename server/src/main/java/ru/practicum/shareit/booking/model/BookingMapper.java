package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.user.dto.UserBookingDto;


public enum BookingMapper {
    INSTANT;

    public BookingDto toBookingDto(Booking booking, ItemBookingDto item, UserBookingDto user) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setItem(item);
        bookingDto.setBooker(user);
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }
}
