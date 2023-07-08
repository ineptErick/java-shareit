package ru.practicum.shareit.exeptions;

public class ModelValidationException extends RuntimeException {

    public ModelValidationException(final String message) {
        super(message);
    }
}