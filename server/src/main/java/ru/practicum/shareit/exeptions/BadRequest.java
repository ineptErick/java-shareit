package ru.practicum.shareit.exeptions;

public class BadRequest extends RuntimeException {

    public BadRequest(final String message) {
        super(message);
    }
}