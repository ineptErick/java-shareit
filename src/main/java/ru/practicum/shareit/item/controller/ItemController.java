package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping()
    public ItemDto createItem(@RequestHeader(value = "X-Sharer-User-Id") int userId,
                              @Valid @RequestBody ItemDto itemDto) {
        return itemService.createItem(itemDto, userId);
    }
    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable int itemId,
                              @RequestBody ItemDto itemDto,
                              @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return itemService.updateItem(itemDto, itemId, userId);
    }
    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable int id) {
        itemService.deleteItem(id);
    }

    @GetMapping("/{itemId}")
    public  ItemDto getItemById(@PathVariable int itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping()
    public List<ItemDto> getItems(@RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsByText(@RequestParam String text) {
        return itemService.searchItemByText(text);
    }

}
