package ru.practicum.shareit.item.services;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemReplyDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemReplyDto createItem(ItemCreationDto itemCreationDto, Long userId);

    Item getItemById(Long itemId);

    List<ItemReplyDto> getItems(Long userId, Integer from, Integer size);

    void deleteItem(Long id);

    ItemReplyDto updateItem(ItemCreationDto itemCreationDto, Long itemId, Long userId);

    ItemReplyDto getItemDtoById(Long itemId, Long userId);

    List<ItemReplyDto> searchItemByText(String text);

    CommentDto createComment(CommentRequestDto commentDto, Long itemId, Long userId);
}
