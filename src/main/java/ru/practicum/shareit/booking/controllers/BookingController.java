package ru.practicum.shareit.booking.controllers;

// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.SentBookingDto;
import ru.practicum.shareit.booking.dto.ReceivedBookingDto;
import ru.practicum.shareit.booking.services.BookingService;
import ru.practicum.shareit.user.dto.Create;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final BookingService bookingService;

    // @Autowired
    public BookingController(BookingService bookingService) {
       this.bookingService = bookingService;
    }

    @GetMapping("/{bookingId}")
    public SentBookingDto getBooking(@PathVariable Long bookingId,
                                     @RequestHeader(value = USER_ID) Long userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping()
    public List<SentBookingDto> getAllUserBookings(@RequestHeader(value = USER_ID) Long userId,
                                                   @RequestParam(name = "from", required = false) Integer from,
                                                   @RequestParam(name = "size", required = false) Integer size,
                                                   @RequestParam(name = "state", defaultValue = "ALL")
                                                       String state) {
        return bookingService.getAllUserBookings(userId, state, "USER", from, size);
    }

    @GetMapping("/owner")
    public List<SentBookingDto> getAllOwnerBookings(@RequestHeader(value = USER_ID) Long userId,
                                                    @RequestParam(name = "from", required = false) Integer from,
                                                    @RequestParam(name = "size", required = false) Integer size,
                                                    @RequestParam(name = "state",
                                                            defaultValue = "ALL") String state) {
        return bookingService.getAllUserBookings(userId, state, "OWNER", from, size);
    }

    @PostMapping()
    public SentBookingDto createBooking(@Validated(Create.class)
                                        @RequestBody ReceivedBookingDto bookingDto,
                                        @RequestHeader(value = USER_ID) Long userId) {
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public SentBookingDto updateBookingStatus(@PathVariable Long bookingId,
                                              @RequestParam(name = "approved") String approved,
                                              @RequestHeader(value = USER_ID) Long userId) {
        return bookingService.updateBookingStatus(bookingId, approved.toLowerCase(), userId);
    }
}
