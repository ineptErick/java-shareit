package ru.practicum.shareit.booking.services;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
    public SentBookingDto getBooking(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + bookingId));
        if (booking.getBooker().getId() == userId || booking.getItem().getOwner() == userId) {
            return convertBookingToDto(booking);
        } else {
            throw new InappropriateUserException("Inappropriate User: " + userId);
        }
    }

    @Transactional(readOnly = true)
    public List<SentBookingDto> getAllUserBookings(long userId, String state, String userType) {
        if (Arrays.stream(BookingState.values()).noneMatch(enumState -> enumState.name().equals(state))) {
            log.debug("booking not found for user {}", userId);
            throw new UnsupportedStatusException("Unknown state: " + state);
            // Не забываем также передать в сообщение об ошибке тот state, что был передан
            // - done
        }
        userService.isExistUser(userId);
        List<Booking> userBookings = userType.equals(USER)
                ? bookingRepository.findAllUserBookingsByState(userId, state)
                : bookingRepository.findAllOwnerBookingsByState(userId, state);
        return convertListBookingToDto(userBookings);
    }

    @Transactional
    public SentBookingDto createBooking(ReceivedBookingDto bookingDto, long userId) {
        isValidBookingTimeRequest(bookingDto);
        Item item = itemService.getItemById(bookingDto.getItemId());
        isValidBookingItemRequest(item, userId);
        Booking booking = convertDtoToBooking(bookingDto);
        booking.setItem(item);
        booking.setBooker(userService.getUserById(userId));
        return convertBookingToDto(bookingRepository.save(booking));
    }

    @Transactional
    public SentBookingDto updateBookingStatus(long bookingId, String approved, long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));
        isValidUpdateBookingStatusRequest(booking, userId, bookingId);
        setBookingStatus(booking, approved);
        return convertBookingToDto(bookingRepository.save(booking));
    }

    private void isValidBookingTimeRequest(ReceivedBookingDto bookingDto) {
        if (
            // Часть проверок можно будет исключить,
            // так как теперь они реализованы с помощью аннотаций валидации
            // - done

            // (предложение) Если будет интересно, то эту валидацию можно было бы реализовать с помощью аннотации валидации над классом,
            // чтобы поддерживать декларативный подход
            //https://devcolibri.com/spring-mvc-кастомная-аннотация-для-валидации/
            //Кстати, если будет интересно то, как реализовать кастомную аннотацию валидации
            // на уровне класса ReceivedBookingDto
            // https://github.com/TyutterinYakov/CustomValidationClassLevel
            // - изучила, но решила пока не делать этого в данном проекте

                bookingDto.getStart().compareTo(bookingDto.getEnd()) >= 0) {
            throw new BadRequestException("Not valid fields");
        }
    }

    private void isValidBookingItemRequest(Item item, long userId) {
        if (item.getAvailable().equals(false)) {
            throw new ItemIsUnavailableException("Item " + item.getId() + "is unavailable");
        }
        if (item.getOwner() == userId) {
            throw new InappropriateUserException("Owner cant booking own item");
        }
    }

    private void isValidUpdateBookingStatusRequest(Booking booking, long userId, long bookingId) {
        if (booking.getItem().getOwner() != userId) {
            throw new InappropriateUserException("Inappropriate User: " + userId);
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new BookingStatusAlreadySetException("Booking status already set: " + bookingId);
        }
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
