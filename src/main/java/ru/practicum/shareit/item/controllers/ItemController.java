package ru.practicum.shareit.item.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemReplyDto;
import ru.practicum.shareit.item.services.ItemService;
import ru.practicum.shareit.item.dto.ItemCreationDto;

import javax.validation.Valid;
import java.util.List;

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
    public ItemReplyDto getItemById(@PathVariable Long itemId,
                                    @RequestHeader(value = USER_ID) Long userId) {
        return itemService.getItemDtoById(itemId, userId);
    }

    @GetMapping()
    public List<ItemReplyDto> getItems(@RequestHeader(value = USER_ID) Long userId,
                                       @RequestParam(name = "from", required = false) Integer from,
                                       @RequestParam(name = "size", required = false) Integer size) {
        return itemService.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemReplyDto> searchItemsByText(@RequestParam String text) {
        return itemService.searchItemByText(text);
    }

    @PostMapping()
    public ItemReplyDto createItem(@RequestHeader(value = USER_ID) Long userId,
                                   @Valid @RequestBody ItemCreationDto itemCreationDto) {
        return itemService.createItem(itemCreationDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@Valid @RequestBody CommentRequestDto commentRequestDto,
                                    @PathVariable Long itemId,
                                    @RequestHeader(value = USER_ID) Long userId) {
        return itemService.createComment(commentRequestDto, itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemReplyDto updateItem(@PathVariable Long itemId,
                                   @RequestBody ItemCreationDto itemCreationDto,
                                   @RequestHeader(value = USER_ID) Long userId) {
        return itemService.updateItem(itemCreationDto, itemId, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
    }

}
