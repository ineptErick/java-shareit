package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;

@Component("dbBookingRepository")
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingDto(bk.id, bk.start, bk.end, bk.status, booker.id, it.id, it.name) " +
            "FROM Booking AS bk " +
            "JOIN Item AS it ON bk.itemId = it.id " +
            "JOIN User AS booker ON bk.bookerId = booker.id " +
            "WHERE booker.id = ?1 " +
            "ORDER BY bk.start DESC ")
    Slice<BookingDto> userFindAll(Long userId, Pageable pageable);

    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingDto(bk.id, bk.start, bk.end, bk.status, booker.id, it.id, it.name) " +
            "FROM Booking AS bk " +
            "JOIN Item AS it ON bk.itemId = it.id " +
            "JOIN User AS booker ON bk.bookerId = booker.id " +
            "WHERE booker.id = ?1 " +
            "AND bk.start < ?2 " +
            "AND bk.end > ?3 " +
            "ORDER BY bk.start DESC ")
    Slice<BookingDto> userFindAllCurrent(Long userId, LocalDateTime currentDateTimeStart, LocalDateTime currentDateTimeEnd, Pageable pageable);

    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingDto(bk.id, bk.start, bk.end, bk.status, booker.id, it.id, it.name) " +
            "FROM Booking AS bk " +
            "JOIN Item AS it ON bk.itemId = it.id " +
            "JOIN User AS booker ON bk.bookerId = booker.id " +
            "WHERE booker.id = ?1 " +
            "AND bk.end < ?2 " +
            "ORDER BY bk.start DESC ")
    Slice<BookingDto> userFindAllPast(Long userId, LocalDateTime now, Pageable pageable);

    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingDto(bk.id, bk.start, bk.end, bk.status, booker.id, it.id, it.name) " +
            "FROM Booking AS bk " +
            "JOIN Item AS it ON bk.itemId = it.id " +
            "JOIN User AS booker ON bk.bookerId = booker.id " +
            "WHERE booker.id = ?1 " +
            "AND bk.status IN ('WAITING','APPROVED') " +
            "AND bk.end > ?2 " +
            "ORDER BY bk.start DESC ")
    Slice<BookingDto> userFindAllFuture(Long userId, LocalDateTime now, Pageable pageable);

    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingDto(bk.id, bk.start, bk.end, bk.status, booker.id, it.id, it.name) " +
            "FROM Booking AS bk " +
            "JOIN Item AS it ON bk.itemId = it.id " +
            "JOIN User AS booker ON bk.bookerId = booker.id " +
            "WHERE booker.id = ?1 " +
            "AND bk.status = ?2 " +
            "ORDER BY bk.start DESC ")
    Slice<BookingDto> userFindAllWaitingOrRejected(Long userId, String status, Pageable pageable);

    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingDto(bk.id, bk.start, bk.end, bk.status, booker.id, it.id, it.name) " +
            "FROM Booking AS bk " +
            "JOIN Item AS it ON bk.itemId = it.id " +
            "JOIN User AS booker ON bk.bookerId = booker.id " +
            "WHERE it.owner = ?1 " +
            "ORDER BY bk.start DESC ")
    Slice<BookingDto> ownerFindAll(Long userId, Pageable pageable);

    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingDto(bk.id, bk.start, bk.end, bk.status, booker.id, it.id, it.name) " +
            "FROM Booking AS bk " +
            "JOIN Item AS it ON bk.itemId = it.id " +
            "JOIN User AS booker ON bk.bookerId = booker.id " +
            "WHERE it.owner = ?1 " +
            "AND bk.start < ?2 " +
            "AND bk.end > ?3 " +
            "ORDER BY bk.start DESC ")
    Slice<BookingDto> ownerFindAllCurrent(Long userId, LocalDateTime currentDateTimeStart, LocalDateTime currentDateTimeEnd, Pageable pageable);

    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingDto(bk.id, bk.start, bk.end, bk.status, booker.id, it.id, it.name) " +
            "FROM Booking AS bk " +
            "JOIN Item AS it ON bk.itemId = it.id " +
            "JOIN User AS booker ON bk.bookerId = booker.id " +
            "WHERE it.owner = ?1 " +
            "AND bk.end < ?2 " +
            "ORDER BY bk.start DESC ")
    Slice<BookingDto> ownerFindAllPast(Long userId, LocalDateTime now, Pageable pageable);

    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingDto(bk.id, bk.start, bk.end, bk.status, booker.id, it.id, it.name) " +
            "FROM Booking AS bk " +
            "JOIN Item AS it ON bk.itemId = it.id " +
            "JOIN User AS booker ON bk.bookerId = booker.id " +
            "WHERE it.owner = ?1 " +
            "AND bk.status IN ('WAITING','APPROVED') " +
            "AND bk.end > ?2 " +
            "ORDER BY bk.start DESC ")
    Slice<BookingDto> ownerFindAllFuture(Long userId, LocalDateTime now, Pageable pageable);

    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingDto(bk.id, bk.start, bk.end, bk.status, booker.id, it.id, it.name) " +
            "FROM Booking AS bk " +
            "JOIN Item AS it ON bk.itemId = it.id " +
            "JOIN User AS booker ON bk.bookerId = booker.id " +
            "WHERE it.owner = ?1 " +
            "AND bk.status = ?2 " +
            "ORDER BY bk.start DESC ")
    Slice<BookingDto> ownerFindAllWaitingOrRejected(Long userId, String status, Pageable pageable);

    @Query(value = "SELECT * " +
            "FROM Bookings b " +
            "WHERE b.item_id = ?1 " +
            "AND b.start_date < ?2 " +
            "ORDER BY b.end_date DESC LIMIT 1 ", nativeQuery = true)
    Booking getLastBooking(Long itemId, LocalDateTime now);

    @Query(value = "SELECT * " +
            "FROM Bookings b " +
            "WHERE b.item_id = ?1 " +
            "AND b.start_date > ?2 " +
            "AND NOT b.status = 'REJECTED' " +
            "ORDER BY b.end_date ASC LIMIT 1 ", nativeQuery = true)
    Booking getNextBooking(Long itemId, LocalDateTime now);

    @Query(value = "SELECT * " +
            "FROM Bookings b " +
            "WHERE b.booker_id = ?1 " +
            "AND b.item_id  = ?2 " +
            "AND b.end_date < ?3 " +
            "ORDER BY b.end_date DESC LIMIT 1 ", nativeQuery = true)
    Booking getBookingForComment(Long bookerId, Long itemId, LocalDateTime now);


    Booking getBookingById(Long bookingId);
}