package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.validation.BookingValidation;
import ru.practicum.shareit.exeptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.validation.ItemValidation;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.validation.UserValidation;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserValidation userValidation;
    private final ItemValidation itemValidation;
    private final BookingValidation bookingValidation;

    @Override
    public BookingDto saveBooking(Booking booking, Long userId) {
        Item item = itemValidation.isPresent(booking.getItemId());
        itemValidation.isAvailable(booking.getItemId());
        User user = userValidation.isPresent(userId);
        if (item.getOwner().equals(userId)) {
            throw new NotFoundException("Свою вещь бронировать ненужно.");
        }
        booking.setStatus(BookingStatus.WAITING.toString());
        booking.setBookerId(userId);
        bookingValidation.bookingValidation(booking);
        bookingRepository.save(booking);
        return BookingMapper.INSTANT.toBookingDto(booking,
                ItemMapper.INSTANT.toItemBookingDto(item),
                UserMapper.INSTANT.toUserBookingDto(user));

    }

    @Transactional
    @Override
    public BookingDto acceptBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingValidation.isPresent(bookingId);
        userValidation.isPresent(userId);
        User booker = userValidation.isPresent(booking.getBookerId());
        Item item = itemValidation.isPresent(booking.getItemId());
        if (booking.getStatus().equals(BookingStatus.APPROVED.toString())) {
            throw new BadRequest("Бронирование уже подтверждено.");
        }
        if (!item.getOwner().equals(userId)) {
            log.error("Только владелец может подтвердить бронь.");
            throw new NotFoundException("Только владелиц может подтвердить бронь.");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED.toString());
        } else {
            booking.setStatus(BookingStatus.REJECTED.toString());
        }
        bookingRepository.save(booking);
        return BookingMapper.INSTANT.toBookingDto(booking,
                ItemMapper.INSTANT.toItemBookingDto(item),
                UserMapper.INSTANT.toUserBookingDto(booker));
    }

    @Override
    public List<Booking> getAllItems() {
        return bookingRepository.findAll();
    }

    @Transactional
    @Override
    public BookingDto getBooking(Long bookingId, Long userId) {
        Booking booking = bookingValidation.isPresent(bookingId);
        Item item = itemValidation.isPresent(booking.getItemId());
        /*Заполняется далее. Значение зависит от того, кто кинул запрос (пользователь или владелец).
          Нужен далее в маппере*/
        User user = new User();
        if (booking.getBookerId().equals(userId)) {
            user = userValidation.isPresent(userId);
        } else if (item.getOwner().equals(userId)) {
            user = userValidation.isPresent(booking.getBookerId());
        }
        if (!(booking.getBookerId().equals(userId)) && !(item.getOwner().equals(userId))) {
            throw new NotFoundException("Только арендодатель и арендатор могут просматривать данное бронирование.");
        }
        return BookingMapper.INSTANT.toBookingDto(booking,
                ItemMapper.INSTANT.toItemBookingDto(item),
                UserMapper.INSTANT.toUserBookingDto(user));
    }

    @Override
    public List<BookingDto> getBookingByState(Long userId, String state, Pageable pageable, Boolean isOwner) {
//        if (Arrays.stream(BookingState.values()).noneMatch(bookingState -> bookingState.toString().equals(state))) {
//            throw new BadRequest(String.format("Unknown state: %s", state));
//        }
        userValidation.isPresent(userId);
        Slice<BookingDto> bookingDtoSlice = getSliceOfBookingDto(userId, state, pageable, isOwner);
        while (!bookingDtoSlice.hasContent() && bookingDtoSlice.getNumber() > 0) {
            bookingDtoSlice = getSliceOfBookingDto(userId, state, PageRequest.of(bookingDtoSlice.getNumber() - 1, bookingDtoSlice.getSize()), isOwner);
        }
        return bookingDtoSlice.toList();
    }

    private Slice<BookingDto> getSliceOfBookingDto(Long userId, String state, Pageable pageable, Boolean isOwner) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        switch (state) {
            case "CURRENT":
                return isOwner ? bookingRepository.ownerFindAllCurrent(userId,  currentDateTime, currentDateTime, pageable) : bookingRepository.userFindAllCurrent(userId,  currentDateTime, currentDateTime, pageable);
            case "PAST":
                return isOwner ? bookingRepository.ownerFindAllPast(userId, LocalDateTime.now(), pageable) : bookingRepository.userFindAllPast(userId, LocalDateTime.now(), pageable);
            case "FUTURE":
                return isOwner ? bookingRepository.ownerFindAllFuture(userId, LocalDateTime.now(), pageable) : bookingRepository.userFindAllFuture(userId, LocalDateTime.now(), pageable);
            case "WAITING":
                return isOwner ? bookingRepository.ownerFindAllWaitingOrRejected(userId, state, pageable) : bookingRepository.userFindAllWaitingOrRejected(userId, state, pageable);
            case "REJECTED":
                return isOwner ? bookingRepository.ownerFindAllWaitingOrRejected(userId, state, pageable) : bookingRepository.userFindAllWaitingOrRejected(userId, state, pageable);
            default:
                return isOwner ? bookingRepository.ownerFindAll(userId, pageable) : bookingRepository.userFindAll(userId, pageable);
        }
    }
}