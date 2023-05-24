package ru.practicum.shareit.item.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.InappropriateUserException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemReplyDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.CommentRepository;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import ru.practicum.shareit.user.repositories.UserRepository;
import ru.practicum.shareit.util.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Transactional
public class ItemServiceImplIntegrationTest {
    @Autowired
    private ItemService itemService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testGetItems() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");
        User savedUser = userRepository.save(user);

        Item item1 = new Item();
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setAvailable(true);
        item1.setOwner(savedUser.getId());
        Item savedItem1 = itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setAvailable(false);
        item2.setOwner(savedUser.getId());
        Item savedItem2 = itemRepository.save(item2);

        Comment comment1 = new Comment();
        comment1.setAuthorName(savedUser.getName());
        comment1.setText("Comment 1");
        comment1.setItem(savedItem1);
        commentRepository.save(comment1);

        Comment comment2 = new Comment();
        comment2.setAuthorName(savedUser.getName());
        comment2.setText("Comment 2");
        comment2.setItem(savedItem1);
        commentRepository.save(comment2);

        List<ItemReplyDto> items = itemService.getItems(savedUser.getId(), null, null);

        assertEquals(2, items.size());
        assertEquals(savedItem1.getId(), items.get(0).getId());
        assertEquals("Item 1", items.get(0).getName());
        assertEquals("Description 1", items.get(0).getDescription());
        assertTrue(items.get(0).getAvailable());

        assertEquals(savedItem2.getId(), items.get(1).getId());
        assertEquals("Item 2", items.get(1).getName());
        assertEquals("Description 2", items.get(1).getDescription());
        assertFalse(items.get(1).getAvailable());
    }

    @Test
    public void testSearchItemByText_whenTextIsEmpty_shouldReturnEmptyList() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");
        User savedUser = userRepository.save(user);

        Item item1 = new Item();
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setAvailable(true);
        item1.setOwner(savedUser.getId());
        itemRepository.save(item1);
        String text = "";

        List<ItemReplyDto> result = itemService.searchItemByText(text);

        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    public void testSearchItemByText_whenMatchingItemsExist_shouldReturnList() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");
        User savedUser = userRepository.save(user);

        User user1 = new User();
        user1.setName("asd");
        user1.setEmail("asd@example.com");
        userRepository.save(user1);

        Item item1 = new Item();
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setAvailable(true);
        item1.setOwner(savedUser.getId());
        itemRepository.save(item1);

        String text = "item";

        List<ItemReplyDto> result = itemService.searchItemByText(text);

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualToIgnoringCase("item 1");
    }

    @Test
    public void updateItem_WithValidData_ShouldUpdateItem() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");
        User savedUser = userRepository.save(user);

        Item item = new Item();
        item.setName("Item 1");
        item.setDescription("Description 1");
        item.setAvailable(true);
        item.setOwner(savedUser.getId());
        itemRepository.save(item);

        ItemCreationDto itemDto = new ItemCreationDto();
        itemDto.setDescription("update");

        ItemReplyDto updatedItemDto = itemService.updateItem(itemDto, item.getId(), user.getId());

        assertEquals(itemDto.getDescription(), updatedItemDto.getDescription());
        assertEquals(updatedItemDto.getName(), item.getName());
    }

    @Test
    void updateItem_WithInvalidUserId_ShouldThrowInappropriateUserException() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");
        User savedUser = userRepository.save(user);

        Item item = new Item();
        item.setName("Item 1");
        item.setDescription("Description 1");
        item.setAvailable(true);
        item.setOwner(savedUser.getId());
        itemRepository.save(item);

        ItemCreationDto itemDto = new ItemCreationDto(); // мб тут reply dto, проверь
        itemDto.setDescription("update");

        assertThrows(InappropriateUserException.class, () -> itemService.updateItem(itemDto, item.getId(), 1000L));
    }

    @Test
    void updateItem_WithInvalidUserId_ShouldThrowEntityNotFoundException() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");
        User savedUser = userRepository.save(user);

        Item item = new Item();
        item.setName("Item 1");
        item.setDescription("Description 1");
        item.setAvailable(true);
        item.setOwner(savedUser.getId());
        itemRepository.save(item);

        ItemCreationDto itemDto = new ItemCreationDto();
        itemDto.setDescription("update");

        assertThrows(EntityNotFoundException.class, () -> itemService.updateItem(itemDto, 1000L, savedUser.getId()));
    }

    @Test
    void createComment_WithValidData_ShouldCreateComment() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");
        userRepository.save(user);

        Item item = new Item();
        item.setName("Item 1");
        item.setDescription("Description 1");
        item.setAvailable(true);
        item.setOwner(user.getId());
        itemRepository.save(item);

        Booking b1 = new Booking();
        b1.setBooker(user);
        b1.setStart(now.minusHours(2));
        b1.setEnd(now.minusHours(1));
        b1.setStatus(BookingStatus.APPROVED);
        b1.setItem(item);
        bookingRepository.save(b1);

        CommentRequestDto commentDto = new CommentRequestDto();
        commentDto.setText("Test comment");

        CommentDto createdComment = itemService.createComment(commentDto, item.getId(), user.getId());

        assertNotNull(createdComment);
        assertEquals(commentDto.getText(), createdComment.getText());
        assertEquals(user.getName(), createdComment.getAuthorName());
    }

    @Test
    void createComment_WithInvalidUserId_ShouldThrowBadRequestException() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");
        userRepository.save(user);

        Item item = new Item();
        item.setName("Item 1");
        item.setDescription("Description 1");
        item.setAvailable(true);
        item.setOwner(user.getId());
        itemRepository.save(item);

        CommentRequestDto commentDto = new CommentRequestDto();
        commentDto.setText("Test comment");

        assertThrows(BadRequestException.class, () -> {
            itemService.createComment(commentDto, item.getId(), Long.MAX_VALUE);
        });
    }

    @Test
    void createComment_WithEmptyText_ShouldThrowBadRequestException() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");
        userRepository.save(user);

        Item item = new Item();
        item.setName("Item 1");
        item.setDescription("Description 1");
        item.setAvailable(true);
        item.setOwner(user.getId());
        itemRepository.save(item);

        Booking b1 = new Booking();
        b1.setBooker(user);
        b1.setStart(now.minusHours(2));
        b1.setEnd(now.minusHours(1));
        b1.setStatus(BookingStatus.APPROVED);
        b1.setItem(item);
        bookingRepository.save(b1);

        CommentRequestDto commentDto = new CommentRequestDto();
        commentDto.setText("");

        assertThrows(BadRequestException.class, () -> {
            itemService.createComment(commentDto, item.getId(), user.getId());
        });
    }

    @Test
    void createItem_WithValidData_ShouldCreateItem() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");
        User savedUser = userRepository.save(user);

        ItemCreationDto itemDto = new ItemCreationDto();
        itemDto.setName("Item 1");
        itemDto.setDescription("Description 1");
        itemDto.setAvailable(true);

        ItemReplyDto savedItem = itemService.createItem(itemDto, savedUser.getId());

        assertNotNull(itemDto);
        assertEquals(itemDto.getName(), savedItem.getName());
    }

    @Test
    void createItem_WithValidData_ShouldThrowException() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");
        userRepository.save(user);

        ItemCreationDto itemDto = new ItemCreationDto();
        itemDto.setName("Item 1");
        itemDto.setDescription("Description 1");
        itemDto.setAvailable(true);

        assertThrows(EntityNotFoundException.class, () -> itemService.createItem(itemDto, 1000L));
    }

    @Test
    public void getItems_WithPagination_ShouldReturnItems() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");
        userRepository.save(user);

        Item item1 = new Item();
        item1.setName("Test Item 1");
        item1.setDescription("Test Item 1 Description");
        item1.setOwner(user.getId());
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("Test Item 2");
        item2.setDescription("Test Item 2 Description");
        item2.setOwner(user.getId());
        itemRepository.save(item2);

        List<ItemReplyDto> items = itemService.getItems(user.getId(), 0, 1);

        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals("Test Item 1", items.get(0).getName());
    }

    @Test
    public void testGetItemDtoById_WithValidItemIdAndUserId_ShouldReturnItemDto() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");
        userRepository.save(user);

        Item item = new Item();
        item.setName("Test item");
        item.setOwner(user.getId());
        item.setDescription("Test description");
        itemRepository.save(item);

        ItemReplyDto itemDto = itemService.getItemDtoById(item.getId(), user.getId());

        assertNotNull(itemDto);
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
    }
}