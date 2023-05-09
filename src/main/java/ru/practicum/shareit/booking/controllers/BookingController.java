package ru.practicum.shareit.booking.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.SentBookingDto;
import ru.practicum.shareit.booking.dto.ReceivedBookingDto;
import ru.practicum.shareit.booking.services.BookingService;
import ru.practicum.shareit.user.dto.Create;

import java.util.List;

// Также можно воспользоваться аннотацией @RequiredArgsConstructor из lombok,
// благодаря которой автоматически будет сгенерирован конструктор для финальных полей
// - done
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@Validated
public class BookingController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public SentBookingDto getBooking(@PathVariable long bookingId,
                                     @RequestHeader(value = USER_ID) long userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    // Если указано defaultValue, это уже подразумевает, что параметр не является обязательным,
    // следовательно required = false не требуется
    // - done
    @GetMapping()
    public List<SentBookingDto> getAllUserBookings(@RequestHeader(value = USER_ID) long userId,
                                                   @RequestParam(name = "state",
                                                           defaultValue = "ALL") String state) {
        return bookingService.getAllUserBookings(userId, state, "USER");
    }

    // Если указано defaultValue, это уже подразумевает, что параметр не является обязательным,
    // следовательно required = false не требуется
    // - done
    @GetMapping("/owner")
    public List<SentBookingDto> getAllOwnerBookings(@RequestHeader(value = USER_ID) long userId,
                                                    @RequestParam(name = "state",
                                                            defaultValue = "ALL") String state) {
        return bookingService.getAllUserBookings(userId, state, "OWNER");
    }

    @PostMapping()
    public SentBookingDto createBooking(@Validated(Create.class)
                                            @RequestBody ReceivedBookingDto bookingDto,
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
