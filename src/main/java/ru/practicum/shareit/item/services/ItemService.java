package ru.practicum.shareit.item.services;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemReplyDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemReplyDto createItem(ItemCreationDto itemCreationDto, long userId);

    Item getItemById(long itemId);

    List<ItemReplyDto> getItems(long userId, Integer from, Integer size);

    void deleteItem(long id);

    ItemReplyDto updateItem(ItemCreationDto itemCreationDto, long itemId, long userId);

    ItemReplyDto getItemDtoById(long itemId, long userId);

    List<ItemReplyDto> searchItemByText(String text);

    CommentDto createComment(CommentRequestDto commentDto, long itemId, long userId);
}
