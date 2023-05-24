package ru.practicum.shareit.booking.services;

import ru.practicum.shareit.booking.dto.ReceivedBookingDto;
import ru.practicum.shareit.booking.dto.SentBookingDto;

import java.util.List;

public interface BookingService {

    SentBookingDto getBooking(Long bookingId, Long userId);

    List<SentBookingDto> getAllUserBookings(Long userId, String state, String user, Integer from, Integer size);

    SentBookingDto createBooking(ReceivedBookingDto bookingDto, Long userId);

    SentBookingDto updateBookingStatus(Long bookingId, String approved, Long userId);
}
