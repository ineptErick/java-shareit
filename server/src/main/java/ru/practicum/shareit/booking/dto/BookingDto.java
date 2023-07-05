package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Getter
public class BookingDto {

    @NotNull
    private final Long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final ShortItemDto item;
    private final ShortBookerDto booker;
    private BookingStatus status;

    @RequiredArgsConstructor
    @Getter
    public static class ShortBookerDto {
        @NotNull
        @JsonProperty(value = "id")
        private final long bookerId;
        @JsonProperty(value = "name")
        private final String bookerName;
    }

    @RequiredArgsConstructor
    @Getter
    public static class ShortItemDto {
        @NotNull
        @JsonProperty(value = "id")
        private final long itemId;
        @JsonProperty(value = "name")
        private final String itemName;
    }
}