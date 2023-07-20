package ru.practicum.shareit.exception;

public class BadRequest extends RuntimeException {

    public BadRequest(final String message) {
        super(message);
    }
}