package ru.practicum.shareit.booking.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.ReceivedBookingDto;
import ru.practicum.shareit.booking.dto.SentBookingDto;

import java.util.List;

public interface BookingService {

    @Transactional(readOnly = true)
    SentBookingDto getBooking(int bookingId, int userId);

    List<SentBookingDto> getAllUserBookings(int userId, String state, String user);

    SentBookingDto createBooking(ReceivedBookingDto bookingDto, int userId);

    SentBookingDto updateBookingStatus(int bookingId, String approved, int userId);
}