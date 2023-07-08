package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeptions.BadRequest;
import ru.practicum.shareit.exeptions.ForbiddenException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.validation.ItemValidation;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.validation.UserValidation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserValidation userValidation;
    private final ItemValidation itemValidation;
    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    @Override
    public ItemDto saveItem(Item item, Long userId) {
        itemValidation.itemValidation(item);
        userValidation.isPresent(userId);
        item.setOwner(userId);
        return ItemMapper.INSTANT.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        itemValidation.isPresent(itemId);
        userValidation.isPresent(userId);
        itemDto.setId(itemId);
        Item itemForUpdate = itemRepository.getItemById(itemId);
        isUserIsOwner(itemForUpdate, userId);
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            itemForUpdate.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            itemForUpdate.setDescription(itemDto.getDescription());
        }
        if ((Optional.ofNullable(itemDto.getAvailable()).isPresent())) {
            itemForUpdate.setAvailable(itemDto.getAvailable());
        }
        log.info(String.format("Объект с ID %s успешно обновлён.", itemForUpdate.getId()));
        return ItemMapper.INSTANT.toItemDto(itemRepository.save(itemForUpdate));
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        itemValidation.isPresent(itemId);
        Item item = itemRepository.getItemById(itemId);
        ItemDto itemDto = ItemMapper.INSTANT.toItemDto(item);
        if (item.getOwner().equals(userId)) {
            setBookingDates(itemDto);
        }
        itemDto.setComments(commentRepository.getAllByItemId(itemId));
        return itemDto;
    }

    @Override
    public List<ItemDto> getUsersItems(Long userId, Pageable pageable) {
        userValidation.isPresent(userId);
        Slice<Item> itemSlice = itemRepository.getAllByOwnerOrderById(userId, pageable);
        while (!itemSlice.hasContent() && itemSlice.getNumber() > 0) {
            itemSlice = itemRepository.getAllByOwnerOrderById(userId, PageRequest.of(itemSlice.getNumber() - 1, itemSlice.getSize()));
        }
        List<ItemDto> result = new ArrayList<>();
        ItemDto itemDto = new ItemDto();
        for (Item item : itemSlice) {
            itemDto = ItemMapper.INSTANT.toItemDto(item);
            setBookingDates(itemDto);
            result.add(itemDto);
        }
        return result;
    }

    @Override
    public List<ItemDto> getAllItems() {
        return itemRepository.findAll().stream()
                .map(ItemMapper.INSTANT::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItem(String text, Pageable pageable) {
        Slice<Item> itemSlice = itemRepository.searchByNameOrDescriptionContainingIgnoreCase(text, text, pageable);
        while (!itemSlice.hasContent() && itemSlice.getNumber() > 0) {
            itemSlice = itemRepository.searchByNameOrDescriptionContainingIgnoreCase(text, text, PageRequest.of(itemSlice.getNumber() - 1, itemSlice.getSize()));
        }
        List<ItemDto> result = new ArrayList<>();
        ItemDto itemDto = new ItemDto();
        for (Item item : itemSlice) {
            itemDto = ItemMapper.INSTANT.toItemDto(item);
            setBookingDates(itemDto);
            if (itemDto.getAvailable()) {
                result.add(itemDto);
            }
        }
        return result;
    }

    @Override
    public void deleteItem(Long itemId, Long userId) {
        itemValidation.isPresent(itemId);
        isUserIsOwner(itemRepository.getItemById(itemId), userId);
        itemRepository.deleteById(itemId);
        log.info(String.format("Объект с ID %s успешно удалён.", itemId));
    }

    @Override
    public CommentDto saveComment(CommentDto commentDto, Long itemId, Long userId) {
        Item item = itemValidation.isPresent(itemId);
        User user = userValidation.isPresent(userId);
        commentDto.setCreated(LocalDateTime.now());
        commentDto.setAuthorName(user.getName());
        Booking booking = bookingRepository.getBookingForComment(userId, itemId, commentDto.getCreated());
        if (booking == null) {
            throw new BadRequest("Вы не можете оставить отзыв на вещь, которую еще не брали.");
        }
        Comment comment = commentRepository.save(CommentMapper.INSTANT.toComment(commentDto, itemId, userId));
        commentDto.setId(comment.getId());
        return commentDto;
    }

    private void isUserIsOwner(Item item, Long userId) {
        if (!item.getOwner().equals(userId)) {
            log.error(String.format("Ошибка обновления. Пользователь с ID %s не является владельцем объекта '%s'.",
                    userId, item.getName()));
            throw new ForbiddenException(String.format("Вы не являетесь владельцем объекта '%s'.", item.getName()));
        }
    }

    private void setBookingDates(ItemDto itemDto) {
            Booking lastBooking = bookingRepository.getLastBooking(itemDto.getId(), LocalDateTime.now());
            if (lastBooking != null) {
                itemDto.setLastBooking(new BookingInfoDto(lastBooking.getId(), lastBooking.getBookerId()));
                itemDto.setLastBookingDate(lastBooking.getEnd());
            }
            Booking nextBooking = bookingRepository.getNextBooking(itemDto.getId(), LocalDateTime.now());
        if (nextBooking != null) {
            itemDto.setNextBooking(new BookingInfoDto(nextBooking.getId(), nextBooking.getBookerId()));
            itemDto.setNextBookingDate(nextBooking.getStart());
        }
    }
}
