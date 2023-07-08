package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto saveItem(Item item, Long userId);

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId);

    ItemDto getItemById(Long itemId, Long userId);

    List<ItemDto> getUsersItems(Long userId, Pageable pageable);

    List<ItemDto> getAllItems();

    List<ItemDto> searchItem(String text, Pageable pageable);

    void deleteItem(Long itemId, Long userId);

    CommentDto saveComment(CommentDto commentDto, Long itemId, Long userId);
}