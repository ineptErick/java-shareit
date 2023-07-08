package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exeptions.BadRequest;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestBody Item item,
                           @RequestHeader (value = "X-Sharer-User-Id") Long userId) {
        return itemService.saveItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @RequestHeader (value = "X-Sharer-User-Id") Long userId,
                              @PathVariable Long itemId) {
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId,
                           @RequestHeader (value = "X-Sharer-User-Id") Long userId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getUsersItems(@RequestHeader (value = "X-Sharer-User-Id") Long userId,
                                       @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer page,
                                       @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(50) Integer size) {
        return itemService.getUsersItems(userId, PageRequest.of(page, size));
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text,
                                    @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer page,
                                    @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(50) Integer size) {
        if (!text.isBlank()) {
            return itemService.searchItem(text, PageRequest.of(page, size));
        }
        return new ArrayList<>();
    }

    @GetMapping("/all")
    public List<ItemDto> getAllItems() {
        return itemService.getAllItems();
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Long itemId,
                           @RequestHeader (value = "X-Sharer-User-Id") Long userId) {
        itemService.deleteItem(itemId, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestBody CommentDto commentDto,
                                 @PathVariable Long itemId,
                                 @RequestHeader (value = "X-Sharer-User-Id") Long userId) {
        if (commentDto.getText().isBlank()) {
            throw new BadRequest("Текст отзыва не может быть пустым.");
        }
        return itemService.saveComment(commentDto, itemId, userId);
    }
}