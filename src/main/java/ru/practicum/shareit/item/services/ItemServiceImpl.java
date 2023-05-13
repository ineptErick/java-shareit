package ru.practicum.shareit.item.services;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final LocalDateTime dateNow = LocalDateTime.now();

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
    public List<ItemReplyDto> getItems(long ownerId) {
        List<ItemReplyDto> items = itemRepository.findAllByOwner(ownerId, Sort.by(Sort.Direction.ASC, "id")).stream()
                .map(this::convertItemToDto)
                // Сортировку следует выполнить на уровне запроса к БД, чтобы не производить обработку в коде,
                // так как далее, при работе с пагинацией, это логика станет некорректна,
                // плюс она не совсем эффективна)
                // - done
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
    public CommentDto createComment(CommentRequestDto commentDto, long itemId, long userId) {
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
            item.setName(itemCreationDto.getName());
        }
        if (itemCreationDto.getDescription() != null && !itemCreationDto.getDescription().isBlank()) {
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

    private void isValidComment(CommentRequestDto commentDto, long itemId, long userId) {
        if (!bookingRepository.existsBookingByBooker_IdAndItem_IdAndStatusAndEndBefore(userId, itemId,
                // До текущего времени должна быть не дата старта бронирования, а дата окончания)
                // То есть вместо StartBefore должно использоваться EndBefore,
                // так как оставить комментарий можно только при истекшем бронировании)
                // - done
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
        List<Long> itemsId = items.stream().map(ItemReplyDto::getId).collect(Collectors.toList());

        Map<Long, BookingDate> allLastBooking = bookingRepository.findAllLastBooking(itemsId, dateNow)
                .stream().collect(Collectors.toMap(BookingDate::getItemId, Function.identity(), (o, o1) -> o1));
        // Дату LocalDateTime.now() хорошо было бы вынести в отдельную переменную,
        // и передавать ее в оба метода, чтобы получение осуществлялось для одного момента времени)
        // - done
        // В данном случае должно быть (o, o1) -> o1), так как сортировка выполнена по возрастанию,
        // а бронирование с самым большим временем старта, удовлетворяющее условию и будет последним)
        // - done
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
