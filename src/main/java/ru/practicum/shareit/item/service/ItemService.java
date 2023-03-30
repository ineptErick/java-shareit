package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, int userId);

    Item getItemById(int itemId);

    List<ItemDto> getItems(int userId);

    void deleteItem(int id);

    ItemDto updateItem(ItemDto itemDto, int itemId, int userId);

    ItemDto getItemDtoById(int itemId, int userId);

    List<ItemDto> searchItemByText(String text);

    CommentDto createComment(CommentDto commentDto, int itemId, int userId);
}
