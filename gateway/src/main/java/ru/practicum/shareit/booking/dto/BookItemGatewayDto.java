package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemGatewayDto {

    @Positive(message = "ID вещей должно быть > 0.")
    private Long itemId;
    @FutureOrPresent(message = "Начало бронирования не может быть в прошлом.")
    @NotNull(message = "Не задана дата начала бронирования.")
    private LocalDateTime start;
    @Future(message = "Окончание бронирование должно быть в будущем.")
    @NotNull(message = "Не задана дата окончания бронирования.")
    private LocalDateTime end;

}
