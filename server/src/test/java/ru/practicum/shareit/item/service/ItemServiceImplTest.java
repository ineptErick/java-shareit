package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeptions.BadRequest;
import ru.practicum.shareit.exeptions.ForbiddenException;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.validation.ItemValidation;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.validation.UserValidation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemServiceImplTest {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserValidation userValidation;
    @Autowired
    ItemValidation itemValidation;
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemService itemService;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    ItemRequestRepository itemRequestRepository;

    Item item1ByUser1;
    Long item1ByUser1Id;
    ItemDto item1ByUser1Dto;
    List<ItemDto> itemsByUser1 = new ArrayList<>();


    User user1;
    Long user1Id;
    UserDto user1Dto;


    User user2;
    Long user2Id;
    UserDto user2Dto;

    CommentDto commentDtoFromUser2ToItem1ByUser1 = new CommentDto();
    List<CommentDto> commentDtoListForItem1ByUser1 = new ArrayList<>();

    Booking bookingUser2Item1ByUser1 = new Booking();
    List<Booking> bookingList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "bookings", "comments", "items", "requests", "users");

        itemsByUser1.clear();
        commentDtoListForItem1ByUser1.clear();
        bookingList.clear();

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


        //Создать UserDto
        user1Dto = new UserDto();
        user1Dto = UserMapper.INSTANT.toUserDto(user1);

        user2Dto = new UserDto();
        user2Dto = UserMapper.INSTANT.toUserDto(user2);

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

    }

    @AfterAll()
    void removeAll() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "bookings", "comments", "items", "requests", "users");
    }

    @Test
    void addNewItem() {
        ItemDto actualItemDto = itemService.saveItem(item1ByUser1, user1Id);
        itemsByUser1.add(actualItemDto);
        assertEquals(item1ByUser1Dto, actualItemDto);
    }

    @Test
    void updateItem() {
        item1ByUser1Dto.setName("Item 1 Updated");
        ItemDto actualItemDto = itemService.updateItem(item1ByUser1Dto, item1ByUser1Id, user1Id);
        assertEquals(item1ByUser1Dto, actualItemDto);
    }

    @Test
    void updateItem_Fail_userNotOwner() {
        item1ByUser1Dto.setName("Item 1 Updated");
        Assertions.assertThrows(ForbiddenException.class,
                () -> itemService.updateItem(item1ByUser1Dto, item1ByUser1Id, user2Id));

    }

    @Test
    void getItemById() {
        ItemDto actualItemDto = itemService.getItemById(item1ByUser1Id, user1Id);
        assertEquals(item1ByUser1Dto, actualItemDto);
    }

    @Test
    void getUsersItems() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<ItemDto> actualListItemDto = itemService.getUsersItems(user1Id,pageRequest);
        Assertions.assertEquals(itemsByUser1, actualListItemDto);
    }

    @Test
    void getAllItems() {
        List<ItemDto> actualListItemDto = itemService.getAllItems();
        Assertions.assertEquals(itemsByUser1, actualListItemDto);
    }

    @Test
    void searchItem() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<ItemDto> actualListItemDto = itemService.searchItem("1", pageRequest);
        Assertions.assertEquals(itemsByUser1, actualListItemDto);
    }

    @Test
    void deleteItem() {
        itemService.deleteItem(item1ByUser1Id, user1Id);
        List<ItemDto> actualListItemDto = itemService.getAllItems();
        Assertions.assertEquals(actualListItemDto.size(), 0);
    }

    @Test
    void saveComment() {
        bookingUser2Item1ByUser1.setItemId(item1ByUser1Id);
        bookingUser2Item1ByUser1.setStatus(BookingStatus.APPROVED.toString());
        bookingUser2Item1ByUser1.setStart(LocalDateTime.now().minusDays(30));
        bookingUser2Item1ByUser1.setEnd(LocalDateTime.now().minusDays(10));
        bookingUser2Item1ByUser1.setBookerId(user2Id);
        bookingRepository.save(bookingUser2Item1ByUser1);

        commentDtoFromUser2ToItem1ByUser1.setText("Comment from User 2 to Item1ByUser1");
        commentDtoFromUser2ToItem1ByUser1.setAuthorName(user2.getName());
        commentDtoFromUser2ToItem1ByUser1.setCreated(LocalDateTime.now());
        commentDtoListForItem1ByUser1.add(commentDtoFromUser2ToItem1ByUser1);

        CommentDto actualComment = itemService.saveComment(commentDtoFromUser2ToItem1ByUser1, item1ByUser1Id, user2Id);
        assertEquals(commentDtoFromUser2ToItem1ByUser1, actualComment);

    }

    @Test
    void saveComment_haveNotUsed() {
        Assertions.assertThrows(BadRequest.class, () -> itemService.saveComment(commentDtoFromUser2ToItem1ByUser1, item1ByUser1Id, user2Id));
    }

}