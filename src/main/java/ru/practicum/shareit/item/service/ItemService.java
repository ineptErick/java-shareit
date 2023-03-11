package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, int userId);
    void deleteItem(int userId);
    ItemDto updateItem(ItemDto itemDto, int itemId, int userId);

    List<ItemDto> getItems(int userId);
    ItemDto getItemById(int itemId);
    List<ItemDto> searchItemByText(String text);
}
