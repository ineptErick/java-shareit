package ru.practicum.shareit.booking.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.SentBookingDto;
import ru.practicum.shareit.booking.dto.ReceivedBookingDto;
import ru.practicum.shareit.booking.services.BookingService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/{bookingId}")
    public SentBookingDto getBooking(@PathVariable long bookingId,
                                     @RequestHeader(value = USER_ID) long userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping()
    @Validated
    public List<SentBookingDto> getAllUserBookings(@RequestHeader(value = USER_ID) long userId,
                                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                   @RequestParam(name = "state", defaultValue = "ALL")
                                                   String state) {
        // комментарий: в обоих методах входящий state необходимо проверить на соответствие с существующими и бросить исключение
        // ответ: в сервисе уже прописана проверка и выброс эксепшена
        return bookingService.getAllUserBookings(userId, state, "USER", from, size);
    }

    @GetMapping("/owner")
    @Validated
    public List<SentBookingDto> getAllOwnerBookings(@RequestHeader(value = USER_ID) long userId,
                                                    @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                    @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                    @RequestParam(name = "state",
                                                            required = false, defaultValue = "ALL") String state) {
        // комментарий: в обоих методах входящий state необходимо проверить на соответствие с существующими и бросить исключение
        // ответ: в сервисе уже прописана проверка и выброс эксепшена
        return bookingService.getAllUserBookings(userId, state, "OWNER", from, size);
    }

    @PostMapping()
    public SentBookingDto createBooking(@RequestBody ReceivedBookingDto bookingDto,
                                        @RequestHeader(value = USER_ID) long userId) {
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public SentBookingDto updateBookingStatus(@PathVariable long bookingId,
                                              @RequestParam(name = "approved") String approved,
                                              @RequestHeader(value = USER_ID) long userId) {
        return bookingService.updateBookingStatus(bookingId, approved.toLowerCase(), userId);
    }
}
