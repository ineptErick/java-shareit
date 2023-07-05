package ru.practicum.shareit.booking.dto;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class BookingShortDto {
    private final Long id;
    @NotNull
    private final Long bookerId;
}
