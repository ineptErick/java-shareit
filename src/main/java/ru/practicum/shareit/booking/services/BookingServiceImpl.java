package ru.practicum.shareit.booking.services;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.ReceivedBookingDto;
import ru.practicum.shareit.booking.dto.SentBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.services.ItemService;
import ru.practicum.shareit.user.services.UserService;
import ru.practicum.shareit.util.BookingState;
import ru.practicum.shareit.util.BookingStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;
    private final ModelMapper mapper;
    private static final String USER = "USER";

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              ItemService itemService, UserService userService,
                              ModelMapper mapper) {
        this.bookingRepository = bookingRepository;
        this.itemService = itemService;
        this.userService = userService;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public SentBookingDto getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + bookingId));
        if (booking.getBooker().getId() == userId || booking.getItem().getOwner() == userId) {
            return convertBookingToDto(booking);
        } else {
            throw new InappropriateUserException("Inappropriate User: " + userId);
        }
    }

    @Transactional(readOnly = true)
    public List<SentBookingDto> getAllUserBookings(Long userId, String state, String userType, Integer from, Integer size) {
        if (Arrays.stream(BookingState.values()).noneMatch(enumState -> enumState.name().equals(state))) {
            log.debug("booking not found for user {}", userId);
            throw new UnsupportedStatusException("Unknown state: " + state);
        }
        userService.isExistUser(userId);
        List<Booking> bookings = (from == null && size == null)
                ? getAllUserBookingsWithoutPagination(userId, state, userType)
                : getAllUserBookingsWithPagination(userId, state, userType, from, size);
        return convertListBookingToDto(bookings);
    }

    @Transactional
    public SentBookingDto createBooking(ReceivedBookingDto bookingDto, Long userId) {
        isValidBookingTimeRequest(bookingDto);
        Item item = itemService.getItemById(bookingDto.getItemId());
        isValidBookingItemRequest(item, userId);
        Booking booking = convertDtoToBooking(bookingDto);
        booking.setItem(item);
        booking.setBooker(userService.getUserById(userId));
        return convertBookingToDto(bookingRepository.save(booking));
    }

    @Transactional
    public SentBookingDto updateBookingStatus(Long bookingId, String approved, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found" + bookingId));
        isValidUpdateBookingStatusRequest(booking, userId, bookingId);
        setBookingStatus(booking, approved);
        return convertBookingToDto(bookingRepository.save(booking));
    }

    private void isValidBookingTimeRequest(ReceivedBookingDto bookingDto) {
        if (
                bookingDto.getStart().compareTo(bookingDto.getEnd()) >= 0) {
            throw new BadRequestException("Not valid fields");
        }
    }

    private void isValidBookingItemRequest(Item item, Long userId) {
        if (item.getAvailable().equals(false)) {
            throw new ItemIsUnavailableException("Item " + item.getId() + "is unavailable");
        }
        if (item.getOwner() == userId) {
            throw new InappropriateUserException("Owner cant booking own item");
        }
    }

    private void isValidUpdateBookingStatusRequest(Booking booking, Long userId, Long bookingId) {
        if (booking.getItem().getOwner() != userId) {
            throw new InappropriateUserException("Inappropriate User: " + userId);
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new BookingStatusAlreadySetException("Booking status already set: " + bookingId);
        }
    }

    private List<Booking> getAllUserBookingsWithPagination(Long userId, String state, String userType, Integer from, Integer size) {
        if ((from == 0 && size == 0) || (from < 0 || size < 0)) {
            throw new BadRequestException("Request without pagination");
        }
        PageRequest pageRequest = PageRequest.of(from, size);
        Slice<Booking> bookingsSlice = getBookingSlice(userId, state, userType, pageRequest);
        while (!bookingsSlice.hasContent() && bookingsSlice.getNumber() > 0) {
            bookingsSlice = getBookingSlice(userId, state, userType, PageRequest.of(bookingsSlice.getNumber() - 1, bookingsSlice.getSize(), bookingsSlice.getSort()));
        }
        return bookingsSlice.toList();
    }

    private Slice<Booking> getBookingSlice(long userId, String state, String userType, PageRequest pageRequest) {
        return userType.equals(USER)
                ? bookingRepository.findAllUserBookingsByState(userId, state, pageRequest)
                : bookingRepository.findAllOwnerBookingsByState(userId, state, pageRequest);
    }

    private List<Booking> getAllUserBookingsWithoutPagination(Long userId, String state, String userType) {
        return userType.equals(USER)
                ? bookingRepository.findAllUserBookingsByState(userId, state)
                : bookingRepository.findAllOwnerBookingsByState(userId, state);
    }

    private void setBookingStatus(Booking booking, String approved) {
        if (approved.equals("true")) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus((BookingStatus.REJECTED));
        }
    }

    private SentBookingDto convertBookingToDto(Booking booking) {
        return mapper.map(booking, SentBookingDto.class);
    }

    private Booking convertDtoToBooking(ReceivedBookingDto receivedBookingDto) {
        return mapper.map(receivedBookingDto, Booking.class);
    }

    private List<SentBookingDto> convertListBookingToDto(List<Booking> bookings) {
        return bookings.stream()
                .map(this::convertBookingToDto)
                .collect(Collectors.toList());
    }
}
