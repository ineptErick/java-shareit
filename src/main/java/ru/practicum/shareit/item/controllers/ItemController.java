package ru.practicum.shareit.item.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.services.ItemService;
import ru.practicum.shareit.user.dto.Create;
import ru.practicum.shareit.user.dto.Update;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId,
                               @RequestHeader(value = USER_ID) long userId) {
        return itemService.getItemDtoById(itemId, userId);
    }

    @GetMapping()
    public List<ItemDto> getItems(@RequestHeader(value = USER_ID) long userId,
                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemService.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsByText( @RequestParam(name = "text") String text
                                           // @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                           // @Positive @RequestParam(name = "size", defaultValue = "10") Integer size
                                            ) {
        return itemService.searchItemByText(text);
    }

    @PostMapping()
    public ItemDto createItem( @RequestHeader(value = USER_ID) long userId,
                               @RequestBody @Validated(Create.class) ItemDto item) {
       log.info("Update item by userId={} item={}", userId, item);
        return itemService.createItem(item, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody CommentDto commentDto,
                                    @PathVariable long itemId,
                                    @RequestHeader(value = USER_ID) long userId) {
        return itemService.createComment(commentDto, itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable long itemId,
                              @RequestHeader(value = USER_ID) long userId,
                              @RequestBody @Validated(Update.class) ItemDto item
                              // входящие дто для создания и обновления следует валидировать, при чем по разному.
                              // Для этого были написаны интерфейсы маркеры, которые нужно применить.
                              // - done?
                              ) {
        item.setId(itemId);
        log.info("Update item by userId={} item={}", userId, item);
        return itemService.updateItem(item, itemId, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable long id) {
        itemService.deleteItem(id);
    }

}
