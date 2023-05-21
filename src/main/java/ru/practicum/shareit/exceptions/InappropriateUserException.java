package ru.practicum.shareit.exceptions;

public class InappropriateUserException extends RuntimeException {
    public InappropriateUserException(String message) {
        super(message);
    }
}
