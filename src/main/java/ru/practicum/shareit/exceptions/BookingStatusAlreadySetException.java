package ru.practicum.shareit.exceptions;

public class BookingStatusAlreadySetException extends RuntimeException {
    public BookingStatusAlreadySetException(String message) {
        super(message);
    }
}
