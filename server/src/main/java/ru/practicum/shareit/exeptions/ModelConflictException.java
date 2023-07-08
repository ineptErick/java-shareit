package ru.practicum.shareit.exeptions;

public class ModelConflictException extends RuntimeException {

    public ModelConflictException(final String message) {
        super(message);
    }
}