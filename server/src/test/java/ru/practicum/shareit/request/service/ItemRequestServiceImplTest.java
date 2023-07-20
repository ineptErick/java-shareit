package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import ru.practicum.shareit.exeptions.ModelValidationException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.validation.ItemRequestValidation;
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
class ItemRequestServiceImplTest {

    @Autowired
    ItemRequestRepository itemRequestRepository;

    @Autowired
    ItemRequestService itemRequestService;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserValidation userValidation;

    @Autowired
    ItemRequestValidation itemRequestValidation;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    User user1;
    Long user1Id;
    UserDto user1Dto;

    User user2;
    Long user2Id;
    UserDto user2Dto;

    ItemRequest itemRequest1ByUser1;
    Long itemRequest1IdByUser1;
    ItemRequestDto itemRequest1ByUser1Dto;
    List<ItemRequestDto> itemsRequestsByUser1 = new ArrayList<>();

    @BeforeEach
    void add() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "bookings", "comments", "items", "requests", "users");

        itemsRequestsByUser1.clear();

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

        //Добавляем ItemRequest
        itemRequest1ByUser1 = new ItemRequest();
        itemRequest1ByUser1.setRequesterId(user1Id);
        itemRequest1ByUser1.setDescription("Need new Item");
        itemRequest1ByUser1.setCreated(LocalDateTime.now());
        itemRequestRepository.save(itemRequest1ByUser1);

        itemRequest1IdByUser1 = itemRequestRepository.findAll().get(0).getId();

        itemRequest1ByUser1Dto = new ItemRequestDto();
        itemRequest1ByUser1Dto = ItemRequestMapper.INSTANT.toItemRequestDto(
                itemRequestRepository.getItemRequestById(itemRequest1IdByUser1));

        itemsRequestsByUser1.add(itemRequest1ByUser1Dto);
    }

    @AfterAll()
    void removeAll() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "bookings", "comments", "items", "requests", "users");
    }

    @Test
    void saveRequest_SUCCESS() {
        ItemRequestDto actualItemRequestDto = itemRequestService.saveRequest(itemRequest1ByUser1, user1Id);
        Assertions.assertAll(
                () -> assertEquals(itemRequest1ByUser1Dto.getId(), actualItemRequestDto.getId()),
                () -> assertEquals(itemRequest1ByUser1Dto.getDescription(), actualItemRequestDto.getDescription()),
                () -> assertEquals(itemRequest1ByUser1Dto.getRequestorId(), actualItemRequestDto.getRequestorId()),
                () -> assertEquals(itemRequest1ByUser1Dto.getItems(), actualItemRequestDto.getItems()));
    }

    @Test
    void saveRequest_FAIL_itemRequestNotValid() {
        itemRequest1ByUser1.setDescription(null);
        Assertions.assertThrows(ModelValidationException.class,
                () -> itemRequestService.saveRequest(itemRequest1ByUser1, user1Id));
    }

    @Test
    void findRequestsByOwnerId() {
        List<ItemRequestDto> actualIemRequestList = itemRequestService.findRequestsByOwnerId(user1Id);
        Assertions.assertEquals(itemsRequestsByUser1, actualIemRequestList);
    }

    @Test
    void findAll() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<ItemRequestDto> actualIemRequestList = itemRequestService.findAll(user2Id, pageRequest);
        Assertions.assertEquals(itemsRequestsByUser1, actualIemRequestList);
    }

    @Test
    void getRequestById() {
        ItemRequestDto actualItemRequestDto = itemRequestService.getRequestById(user1Id, itemRequest1IdByUser1);
        assertEquals(itemRequest1ByUser1Dto, actualItemRequestDto);

    }
}