package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemGatewayDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.BadRequest;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(
                                    @RequestBody @Valid BookItemGatewayDto booking,
                                    @Valid @Positive(message = "ID пользователя должен быть > 0.")
                                    @RequestHeader(value = "X-Sharer-User-Id")
                                    Long userId) {
        log.info("Пользователь с userId={} создаёт бронирование booking={}.", userId, booking);
        return bookingClient.addBooking(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateItem(
                                    @Valid @Positive(message = "ID пользователя должен быть > 0.")
                                    @RequestHeader (value = "X-Sharer-User-Id")
                                    Long userId,
                                    @Valid @Positive(message = "ID запроса должно быть > 0.")
                                    @PathVariable
                                    Long bookingId,
                                    @RequestParam Boolean approved) {
        log.info("Пользователь с userId={} отвечает на запрос бронирования с bookingId={}." +
                "Ответ: {}.", userId, bookingId, approved);
        return bookingClient.updateItem(userId, bookingId, approved);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllBooking() {
        log.info("Запрос всех бронирований.");
        return bookingClient.getAllBooking();
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsersBookings(
                                    @Valid @Positive(message = "ID пользователя должен быть > 0.")
                                    @RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                    @RequestParam(name = "state", defaultValue = "ALL") String stateValue,
                                    @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                    @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(50) Integer size) {
        log.info("Пользователь с userId={} запрашивает свои бронирования. Параметры запроса: " +
                        "state={}, from={}, size={}.", userId, stateValue, from, size);
        BookingState.from(stateValue)
                .orElseThrow(() -> new BadRequest("Unknown state: " + stateValue));
        return bookingClient.getAllUsersBookings(userId, stateValue, from, size, false);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllOwnerBookings(
                                    @Valid @Positive(message = "ID пользователя должен быть > 0.")
                                    @RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                    @RequestParam(name = "state", defaultValue = "ALL") String stateValue,
                                    @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                    @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(50) Integer size) {
        log.info("Владелец с userId={} запрашивает бронирования своих вещей. Параметры запроса: " +
                        "state={}, from={}, size={}.", userId, stateValue, from, size);
        BookingState.from(stateValue)
                .orElseThrow(() -> new BadRequest("Unknown state: " + stateValue));
        return bookingClient.getAllOwnerBookings(userId, stateValue, from, size, true);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(
                                    @Valid @Positive(message = "ID запроса должно быть > 0.")
                                    @PathVariable Long bookingId,
                                    @Valid @Positive(message = "ID пользователя должен быть > 0.")
                                    @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Пользователь с userId={} запрашивает информацию о бронирование с bookingId={}.", userId, bookingId);
        return bookingClient.getBooking(bookingId, userId);
    }
}
