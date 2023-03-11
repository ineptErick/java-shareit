package ru.practicum.shareit.item.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EntityNotFound;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ModelMapper mapper;
    private final ItemRepository itemRepository;
    private final UserService userService;
    @Autowired
    public ItemServiceImpl(ModelMapper mapper, ItemRepository itemRepository, UserService userService) {
        this.mapper = mapper;
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, int userId) {
        if (userService.isUserExist(userId)) {
            Item item = convertDtoToItem(itemDto);
            return convertItemToDto(itemRepository.createItem(item, userId));
        } else {
            throw new EntityNotFound("User is not found: " + userId);
        }
    }

    @Override
    public void deleteItem(int userId) {
        itemRepository.deleteItem(userId);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, int itemId, int userId) {
        if (isItemExist(itemId)) {
            Item itemUpdate = convertDtoToItem(itemDto);
            return convertItemToDto(itemRepository.updateItem(itemUpdate, itemId, userId));
        } else {
            throw new EntityNotFound("Item is not found:" + itemId);
        }
    }

    @Override
    public List<ItemDto> getItems(int userId) {
        return itemRepository.getItems(userId).stream()
                .map(this::convertItemToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(int itemId) {
        return convertItemToDto(itemRepository.getItemById(itemId));
    }

    @Override
    public List<ItemDto> searchItemByText(String text) {
        return itemRepository.searchItemByText(text).stream()
                .map(this::convertItemToDto)
                .collect(Collectors.toList());
    }

    private Item convertDtoToItem(ItemDto itemDto) {
        return mapper.map(itemDto, Item.class);
    }
    private ItemDto convertItemToDto(Item item) {
        return mapper.map(item, ItemDto.class);
    }

    public boolean isItemExist(int itemId) {
        return itemRepository.getItemRepo().containsKey(itemId);
    }
}
