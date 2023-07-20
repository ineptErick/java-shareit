package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    BookingDto saveBooking(Booking booking, Long userId);

    BookingDto acceptBooking(Long userId, Long bookingId, Boolean approved);

    List<Booking> getAllItems();

    BookingDto getBooking(Long bookingId, Long userId);

    List<BookingDto> getBookingByState(Long userId, String state, Pageable pageable, Boolean isOwner);
}
