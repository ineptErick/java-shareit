package ru.practicum.shareit.item.services;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.BookingDate;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.InappropriateUserException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemReplyDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repositories.CommentRepository;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.services.UserService;
import ru.practicum.shareit.util.BookingStatus;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Transactional(readOnly = true)
@Service
public class ItemServiceImpl implements ItemService {
    private final ModelMapper mapper;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;

    @Autowired
    public ItemServiceImpl(ModelMapper mapper, ItemRepository itemRepository, BookingRepository bookingRepository, CommentRepository commentRepository, UserService userService) {
        this.mapper = mapper;
        this.mapper.addMappings(skipCommentFieldMap);
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.userService = userService;
    }

    PropertyMap<Item, ItemReplyDto> skipCommentFieldMap = new PropertyMap<Item, ItemReplyDto>() {
        protected void configure() {
            skip().setComments(null);
        }
    };

    @Override
    @Transactional(readOnly = true)
    public ItemReplyDto getItemDtoById(long itemId, long userId) {
        LocalDateTime dateNow = LocalDateTime.now();
        Item item = getItemById(itemId);
        ItemReplyDto dto = convertItemToDto(item);
        if (item.getOwner() == userId) {
            dto.setLastBooking(bookingRepository.findLastBooking(itemId, dateNow));
            dto.setNextBooking(bookingRepository.findNextBooking(itemId, dateNow));
        }
        dto.setComments(commentRepository.findByItem(item).orElse(new HashSet<>()));
        return dto;
    }

    @Override
    public Item getItemById(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found: " + itemId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemReplyDto> getItems(long ownerId, Integer from, Integer size) {
        List<Item> items = getItemsPage(ownerId, from, size);
        List<ItemReplyDto> itemsDto = itemRepository.findAllByOwner(ownerId, Sort.by(Sort.Direction.ASC, "id")).stream()
                .map(this::convertItemToDto)
                .collect(toList());
        setBookingDate(itemsDto);
        return itemsDto;
    }

    @Override
    @Transactional
    public ItemReplyDto createItem(ItemCreationDto itemCreationDto, long userId) {
        userService.isExistUser(userId);
        Item item = convertDtoToItem(itemCreationDto);
        item.setOwner(userId);
        return convertItemToDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public CommentDto createComment(CommentRequestDto commentDto, long itemId, long userId) {
        isValidComment(commentDto, itemId, userId);
        Comment comment = new Comment();
        setCommentField(comment, commentDto, itemId, userId);
        return convertCommentToDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public ItemReplyDto updateItem(ItemCreationDto itemCreationDto, long itemId, long userId) {
        isExistItem(itemId);
        Item item = itemRepository.findById(itemId).get();

        if (item.getOwner() != userId) {
            throw new InappropriateUserException("Item has a different owner" + userId);
        }

        setUpdateItemFields(item, itemCreationDto);
        return convertItemToDto(item);
    }

    @Override
    @Transactional
    public void deleteItem(long userId) {
        itemRepository.deleteById(userId);
    }

    @Override
    public List<ItemReplyDto> searchItemByText(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchItemByText(text).stream()
                .map(this::convertItemToDto)
                .collect(toList());
    }

    private void isValidComment(CommentRequestDto commentDto, long itemId, long userId) {
        if (!bookingRepository.existsBookingByBooker_IdAndItem_IdAndStatusAndEndBefore(userId, itemId,
                BookingStatus.APPROVED, LocalDateTime.now())) {
            throw new BadRequestException("User " + userId + "doesnt use this item " + itemId);
        }
    }

    private void setCommentField(Comment comment, CommentDto commentDto, long itemId, long userId) {
        comment.setText(commentDto.getText());
        comment.setAuthorName(userService.getUserById(userId).getName());
        comment.setItem(getItemById(itemId));
        comment.setCreated(LocalDateTime.now());
    }

    private void setCommentField(Comment comment, CommentRequestDto commentDto, long itemId, long userId) {
        comment.setText(commentDto.getText());
        comment.setAuthorName(userService.getUserById(userId).getName());
        comment.setItem(getItemById(itemId));
        comment.setCreated(LocalDateTime.now());
    }

    private void setUpdateItemFields(Item item, ItemCreationDto itemDto) {
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
    }

    private Item convertDtoToItem(ItemCreationDto itemCreationDto) {
        return mapper.map(itemCreationDto, Item.class);
    }

    private ItemReplyDto convertItemToDto(Item item) {
        return mapper.map(item, ItemReplyDto.class);
    }

    private CommentDto convertCommentToDto(Comment comment) {
        return mapper.map(comment, CommentDto.class);
    }


    public void isExistItem(long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new EntityNotFoundException("Item not found:" + itemId);
        }
    }

    private List<Item> getItemsPage(long ownerId, Integer from, Integer size) {
        if (from == null && size == null) {
            return itemRepository.findAllByOwner(ownerId,Sort.by(Sort.Direction.ASC, "id"));
        } else {
            if ((from == 0 && size == 0) || (from < 0 || size < 0)) {
                throw new BadRequestException("Request without pagination");
            }
            return itemRepository.findAllByOwner(ownerId, PageRequest.of(from, size)).toList();
        }
    }

    private void setBookingDate(List<ItemReplyDto> items) {
        List<Long> itemsId = items.stream().map(ItemReplyDto::getId).collect(Collectors.toList());
        LocalDateTime dateNow = LocalDateTime.now();

        Map<Long, BookingDate> allLastBooking = bookingRepository.findAllLastBooking(itemsId, dateNow)
                .stream().collect(Collectors.toMap(BookingDate::getItemId, Function.identity(), (o, o1) -> o1));
        Map<Long, BookingDate> allNextBooking = bookingRepository.findAllNextBooking(itemsId, dateNow)
                .stream().collect(Collectors.toMap(BookingDate::getItemId, Function.identity(), (o, o1) -> o));

        if (!allNextBooking.isEmpty()) {
            for (ItemReplyDto item : items) {
                item.setNextBooking(allNextBooking.get(item.getId()));
            }
        }
        if (!allLastBooking.isEmpty()) {
            for (ItemReplyDto item : items) {
                item.setLastBooking(allLastBooking.get(item.getId()));
            }
        }
    }
}
