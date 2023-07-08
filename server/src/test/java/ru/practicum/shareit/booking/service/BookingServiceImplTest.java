package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.validation.BookingValidation;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.validation.ItemValidation;
import ru.practicum.shareit.user.dto.UserBookingDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.validation.UserValidation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookingServiceImplTest {

    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    BookingService bookingService;
    @Autowired
    UserValidation userValidation;
    @Autowired
    ItemValidation itemValidation;
    @Autowired
    BookingValidation bookingValidation;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    JdbcTemplate jdbcTemplate;

    Item item1ByUser1;
    Long item1ByUser1Id;
    ItemDto item1ByUser1Dto;

    ItemBookingDto itemBookingDto;
    List<ItemDto> itemsByUser1 = new ArrayList<>();

    User user1;
    Long user1Id;

    User user2;
    Long user2Id;
    UserBookingDto userBookingDto;

    User user3;
    Long user3Id;

    Booking bookingUser2Item1ByUser1;
    Long bookingId;

    BookingDto bookingUser2Item1ByUser1Dto;

    List<Booking> bookingList = new ArrayList<>();

    List<BookingDto> bookingDtoList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "bookings", "comments", "items", "requests", "users");

        bookingList.clear();
        bookingDtoList.clear();
        itemsByUser1.clear();

        //Добавляем пользователей
        user1 = new User();
        user1.setName("User1");
        user1.setEmail("user1@mail.ru");
        userRepository.save(user1);
        user1Id = userRepository.findAll().get(0).getId();
        user1.setId(user1Id);

        user2 = new User();
        user2.setName("User2");
        user2.setEmail("user2@mail.ru");
        userRepository.save(user2);
        user2Id = userRepository.findAll().get(1).getId();
        user2.setId(user2Id);

        userBookingDto = new UserBookingDto(user2Id);

        user3 = new User();
        user3.setName("User3");
        user3.setEmail("user3@mail.ru");
        userRepository.save(user3);
        user3Id = userRepository.findAll().get(2).getId();
        user3.setId(user3Id);

        //Добавляем Item
        item1ByUser1 = new Item();
        item1ByUser1.setName("Item 1");
        item1ByUser1.setDescription("By User 1");
        item1ByUser1.setAvailable(true);
        item1ByUser1.setOwner(user1Id);

        itemRepository.save(item1ByUser1);

        item1ByUser1Dto = ItemMapper.INSTANT.toItemDto(itemRepository.findAll().get(0));
        item1ByUser1Id = item1ByUser1Dto.getId();

        itemsByUser1.add(item1ByUser1Dto);

        itemBookingDto = new ItemBookingDto();
        itemBookingDto.setId(item1ByUser1.getId());
        itemBookingDto.setName(item1ByUser1.getName());

        bookingId = itemBookingDto.getId();



        bookingUser2Item1ByUser1 = new Booking();
        bookingUser2Item1ByUser1.setItemId(item1ByUser1Id);
        bookingUser2Item1ByUser1.setStatus(BookingStatus.WAITING.toString());
        bookingUser2Item1ByUser1.setStart(LocalDateTime.now().plusDays(1));
        bookingUser2Item1ByUser1.setEnd(LocalDateTime.now().plusDays(2));
        bookingUser2Item1ByUser1.setBookerId(user2Id);
        bookingRepository.save(bookingUser2Item1ByUser1);
        bookingUser2Item1ByUser1Dto = new BookingDto();
        bookingUser2Item1ByUser1Dto = BookingMapper.INSTANT.toBookingDto(bookingUser2Item1ByUser1, itemBookingDto, userBookingDto);

        bookingList.add(bookingUser2Item1ByUser1);
        bookingDtoList.add(bookingUser2Item1ByUser1Dto);

    }

    @AfterAll()
    void removeAll() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "bookings", "comments", "items", "requests", "users");
    }

    @Test
    void saveBooking() {
        BookingDto actualBooking = bookingService.saveBooking(bookingUser2Item1ByUser1, user2Id);
        assertEquals(bookingUser2Item1ByUser1Dto, actualBooking);
    }

    @Test
    void saveBooking_FAIL_ByOwner() {
        Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.saveBooking(bookingUser2Item1ByUser1, user1Id));

    }

    @Test
    void acceptBooking_FAIL_notOwner() {
        Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.acceptBooking(user2Id, bookingId, true));
    }

    @Test
    void getAllItems() {
        bookingService.saveBooking(bookingUser2Item1ByUser1, user2Id);
        List<Booking> actualBookingList = bookingService.getAllItems();
        Assertions.assertAll(
                () -> assertEquals(bookingList.get(0).getId(), actualBookingList.get(0).getId()),
                () -> assertEquals(bookingList.get(0).getItemId(), actualBookingList.get(0).getItemId()),
                () -> assertEquals(bookingList.get(0).getBookerId(), actualBookingList.get(0).getBookerId()),
                () -> assertEquals(bookingList.get(0).getStatus(), actualBookingList.get(0).getStatus()),
                () -> Assertions.assertEquals(bookingList.size(), actualBookingList.size()));
    }

    @Test
    void getBooking_ByOtherUser() {
        bookingService.saveBooking(bookingUser2Item1ByUser1, user2Id);
        Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.getBooking(bookingId, user3Id));

    }

    @Test
    void getBookingByState_Owner_ALL() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        bookingService.saveBooking(bookingUser2Item1ByUser1, user2Id);
        List<BookingDto> actualBookingList = bookingService.getBookingByState(user1Id, "ALL", pageRequest, true);
        Assertions.assertAll(
                () -> assertEquals(bookingDtoList.get(0).getId(), actualBookingList.get(0).getId()),
                () -> assertEquals(bookingDtoList.get(0).getItem(), actualBookingList.get(0).getItem()),
                () -> assertEquals(bookingDtoList.get(0).getBooker(), actualBookingList.get(0).getBooker()),
                () -> assertEquals(bookingDtoList.get(0).getStatus(), actualBookingList.get(0).getStatus()),
                () -> Assertions.assertEquals(bookingDtoList.size(), actualBookingList.size()));
    }

}