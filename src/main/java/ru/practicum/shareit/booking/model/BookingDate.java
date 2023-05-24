package ru.practicum.shareit.booking.model;

import java.time.LocalDateTime;

public interface BookingDate {

    LocalDateTime getBookingDate();

    Long getBookerId();

    Long getItemId();
}
