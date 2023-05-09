package ru.practicum.shareit.exceptions;

public class UnsupportedStatusException extends RuntimeException {
    private final String reasonPhrase = "Unknown state: UNSUPPORTED_STATUS";

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public UnsupportedStatusException(String message) {
        super(message);
    }
}
