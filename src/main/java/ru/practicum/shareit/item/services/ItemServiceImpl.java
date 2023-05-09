package ru.practicum.shareit.item.services;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.BookingDate;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.InappropriateUserException;
import ru.practicum.shareit.item.dto.CommentDto;
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
        Item item = getItemById(itemId);
        ItemReplyDto dto = convertItemToDto(item);
        if (item.getOwner() == userId) {
            dto.setLastBooking(bookingRepository.findLastBooking(itemId, LocalDateTime.now()));
            dto.setNextBooking(bookingRepository.findNextBooking(itemId, LocalDateTime.now()));
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
    public List<ItemReplyDto> getItems(long ownerId) {
        List<ItemReplyDto> items = itemRepository.findAllByOwner(ownerId).stream()
                .map(this::convertItemToDto)
                .sorted(Comparator.comparing(ItemReplyDto::getId))
                .collect(toList());

        setBookingDate(items);
        return items;
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
    public CommentDto createComment(CommentDto commentDto, long itemId, long userId) {
        isValidComment(commentDto, itemId, userId);
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setAuthorName(userService.getUserById(userId).getName());
        comment.setItem(getItemById(itemId));
        comment.setCreated(LocalDateTime.now());
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
        if (itemCreationDto.getName() != null && !itemCreationDto.getName().isBlank()) {
            // Хорошо было бы также проверить, что строка не пуста и не состоит только из пробелов
            // Сделать это удобно с помощью метода isBlank
            // - done
            item.setName(itemCreationDto.getName());
        }
        if (itemCreationDto.getDescription() != null && !itemCreationDto.getDescription().isBlank()) {
            // Хорошо было бы также проверить, что строка не пуста и не состоит только из пробелов
            // Сделать это удобно с помощью метода isBlank
            // - done
            item.setDescription(itemCreationDto.getDescription());
        }
        if (itemCreationDto.getAvailable() != null) {
            item.setAvailable(itemCreationDto.getAvailable());
        }
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

    private void isValidComment(CommentDto commentDto, long itemId, long userId) {

        // Более проверка не потребуется, так как она будет реализована с помощью аннотации валидации
        // - done

        if (!bookingRepository.existsBookingByBooker_IdAndItem_IdAndStatusAndStartBefore(userId, itemId,
                // До текущего времени должна быть не дата старта бронирования, а дата окончания
                // - ???
                BookingStatus.APPROVED, LocalDateTime.now())) {
            throw new BadRequestException("User " + userId + "doesnt use this item " + itemId);
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

    private void setBookingDate(List<ItemReplyDto> items) {
        // Следует получить комментарии всех вещей, которые будут возвращены вне цикла
        // и разбить их на мапу, где ключ - идентификатор вещи, а значение - коллекция из комментариев
        // Таким образом мы не будем производить n-лишних запросов к БД
        // Map<Item, List<Comment>> comments = commentRepository.findByItemIn(items, Sort.by(DESC, "created"))
        //				.stream()
        //				.collect(groupingBy(Comment::getItem, toList()));
        // Это лишь предложение по реализации, можно реализовать свою логику,
        // главное избавиться от обращения к методам репозиториев в циклах
        // - реализовала другим способом
        List<Long> itemsId = items.stream().map(ItemReplyDto::getId).collect(Collectors.toList());

        Map<Long, BookingDate> allLastBooking = bookingRepository.findAllLastBooking(itemsId, LocalDateTime.now()).stream().collect(Collectors.toMap(bookingDate -> bookingDate.getItemId(), Function.identity(), (o, o1) -> o));
        Map<Long, BookingDate> allNextBooking = bookingRepository.findAllNextBooking(itemsId, LocalDateTime.now()).stream().collect(Collectors.toMap(bookingDate -> bookingDate.getItemId(), Function.identity(), (o, o1) -> o));

        // Порядок при извлечении из БД - не гарантирован.
        // Следует предварительно разбить коллекцию на мапу, где ключом будет Item(или его идентификатор),
        // а значением - BookingDate, так как иначе эта логика может отработать некорректно
        // Следует добавить в BookingDate и идентификатор вещи, к которой относится бронирование
        // С помощью стримов было бы очень удобно произвести преобразование к мапе
        // - done
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
