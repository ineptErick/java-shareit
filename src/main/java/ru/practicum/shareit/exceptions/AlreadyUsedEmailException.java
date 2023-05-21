package ru.practicum.shareit.exceptions;

public class AlreadyUsedEmailException extends RuntimeException {
    public AlreadyUsedEmailException(String message) {
        super(message);
    }
}
