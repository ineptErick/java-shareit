package ru.practicum.shareit.booking.model;

import java.time.LocalDateTime;

public interface BookingDate {
    Integer getId();

    LocalDateTime getBookingDate();

    Integer getBookerId();
}
