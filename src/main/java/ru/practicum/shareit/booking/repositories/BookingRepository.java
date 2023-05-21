package ru.practicum.shareit.booking.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDate;
import ru.practicum.shareit.util.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM bookings b WHERE b.booker_id = :userId " +
            "AND (:state = 'CURRENT' AND CURRENT_TIMESTAMP BETWEEN b.start_date AND b.end_date " +
            "OR :state = 'PAST' AND b.end_date < CURRENT_TIMESTAMP " +
            "OR :state = 'FUTURE' AND b.start_date > CURRENT_TIMESTAMP " +
            "OR :state = 'WAITING' AND b.status = 'WAITING' " +
            "OR :state = 'REJECTED' AND b.status = 'REJECTED' " +
            "OR :state = 'ALL') " +
            "ORDER BY b.start_date DESC")
    List<Booking> findAllUserBookingsByState(@Param("userId") Long userId, @Param("state") String state);

    @Query(nativeQuery = true, value = "SELECT * FROM bookings b JOIN items i ON b.item_id = i.id WHERE i.owner_id = :ownerId " +
            "AND (:state = 'CURRENT' AND CURRENT_TIMESTAMP BETWEEN b.start_date AND b.end_date " +
            "OR :state = 'PAST' AND b.end_date < CURRENT_TIMESTAMP " +
            "OR :state = 'FUTURE' AND b.start_date > CURRENT_TIMESTAMP " +
            "OR :state = 'WAITING' AND b.status = 'WAITING' " +
            "OR :state = 'REJECTED' AND b.status = 'REJECTED' " +
            "OR :state = 'ALL') " +
            "ORDER BY b.start_date DESC")
    List<Booking> findAllOwnerBookingsByState(@Param("ownerId") Long ownerId, @Param("state") String state);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId " +
            "AND (:state = 'CURRENT' AND CURRENT_TIMESTAMP BETWEEN b.start AND b.end " +
            "OR :state = 'PAST' AND b.end < CURRENT_TIMESTAMP " +
            "OR :state = 'FUTURE' AND b.start > CURRENT_TIMESTAMP " +
            "OR :state = 'WAITING' AND b.status = 'WAITING' " +
            "OR :state = 'REJECTED' AND b.status = 'REJECTED' " +
            "OR :state = 'ALL') " +
            "ORDER BY b.start DESC")
    Slice<Booking> findAllUserBookingsByState(@Param("userId") Long userId, @Param("state") String state, Pageable pageable);
    // !!! Погуглить Sice и Pageable !!!

    @Query("SELECT b FROM Booking b WHERE b.item.owner = :ownerId " +
            "AND (:state = 'CURRENT' AND CURRENT_TIMESTAMP BETWEEN b.start AND b.end " +
            "OR :state = 'PAST' AND b.end < CURRENT_TIMESTAMP " +
            "OR :state = 'FUTURE' AND b.start > CURRENT_TIMESTAMP " +
            "OR :state = 'WAITING' AND b.status = 'WAITING' " +
            "OR :state = 'REJECTED' AND b.status = 'REJECTED' " +
            "OR :state = 'ALL') " +
            "ORDER BY b.start DESC")
    Slice<Booking> findAllOwnerBookingsByState(@Param("ownerId") Long ownerId, @Param("state") String state, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT b.id, b.start_date AS bookingDate, b.booker_id AS bookerId " +
            "FROM bookings b WHERE b.item_id = ?1 AND b.start_date <= ?2 AND b.status = 'APPROVED'" +
            "ORDER BY b.start_date DESC LIMIT 1")
    BookingDate findLastBooking(Long itemId, LocalDateTime currentTime);

    @Query(nativeQuery = true, value = "SELECT b.id, b.start_date AS bookingDate, b.booker_id AS bookerId " +
            "FROM bookings b WHERE b.item_id = ?1 AND b.start_date > ?2 AND b.status = 'APPROVED'" +
            "ORDER BY b.start_date LIMIT 1")
    BookingDate findNextBooking(Long itemId, LocalDateTime currentTime);

    boolean existsBookingByBooker_IdAndItem_IdAndStatusAndEndBefore(Long userId, Long itemId, BookingStatus status, LocalDateTime startDate);

    @Query(nativeQuery = true, value = "SELECT id, b.item_id as itemId, b.start_date AS bookingDate, b.booker_id AS bookerId " +
            "FROM bookings b WHERE b.item_id IN (?1) AND b.start_date > ?2 AND b.status = 'APPROVED' " +
            "ORDER BY b.start_date")
    List<BookingDate> findAllNextBooking(List<Long> itemsId, LocalDateTime currentTime);

    @Query(nativeQuery = true, value = "SELECT id, b.item_id as itemId , b.start_date AS bookingDate, b.booker_id AS bookerId " +
            "FROM bookings b WHERE b.item_id IN (?1) AND b.start_date <= ?2 AND b.status = 'APPROVED' " +
            "ORDER BY b.start_date")
    List<BookingDate> findAllLastBooking(List<Long> itemsId, LocalDateTime currentTime);
}
