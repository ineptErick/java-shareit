package ru.practicum.shareit.exeptions;

public class BookingUnavailableException extends RuntimeException {

    public BookingUnavailableException(final String message) {
        super(message);
    }
}