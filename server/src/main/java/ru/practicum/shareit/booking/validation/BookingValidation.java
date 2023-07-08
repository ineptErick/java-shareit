package ru.practicum.shareit.booking.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeptions.BookingUnavailableException;
import ru.practicum.shareit.exeptions.NotFoundException;

import java.time.LocalDateTime;

@Component("bookingValidation")
@Slf4j
public class BookingValidation {

    @Autowired
    @Qualifier("dbBookingRepository")
    private BookingRepository bookingRepository;

    public void bookingValidation(Booking booking) {
        if (booking.getStart() == null || booking.getEnd() == null) {
            log.error("БРОНИРОВАНИЕ НЕВОЗМОЖНО: Время завершения брони не может быть раньше начала брони.");
            throw new BookingUnavailableException("Время начала и завершения брони должно быть заданно.");
        }
        if (booking.getEnd().isBefore(booking.getStart())) {
            log.error("БРОНИРОВАНИЕ НЕВОЗМОЖНО: Время завершения брони не может быть раньше начала брони.");
            throw new BookingUnavailableException("Время завершения брони не может быть раньше начала брони.");
        }
        if (booking.getEnd().isBefore(LocalDateTime.now()) || booking.getStart().isBefore(LocalDateTime.now())) {
            log.error("БРОНИРОВАНИЕ НЕВОЗМОЖНО: Время завершения брони не может быть раньше начала брони.");
            throw new BookingUnavailableException("Время начала и завершения брони не может быть в прошлом.");
        }
        if (booking.getStart().equals(booking.getEnd())) {
            log.error("БРОНИРОВАНИЕ НЕВОЗМОЖНО: Время завершения брони не может быть раньше начала брони.");
            throw new BookingUnavailableException("Время завершения брони не может быть равным времени начала брони.");
        }
    }

    public Booking isPresent(Long bookingId) {
        if (bookingRepository.findById(bookingId).isEmpty()) {
            log.error(String.format("Бронирование с ID %s не найдено.", bookingId));
            throw new NotFoundException(String.format("Бронирование с ID %d не найдено.", bookingId));
        }
        return bookingRepository.getById(bookingId);
    }
}