package ru.practicum.shareit.item.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.practicum.shareit.exceptions.InappropriateUserException;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemReplyDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.ItemRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void updateItem_WithValidData_ShouldUpdateItem() {
        Long itemId = 1L;
        Long userId = 2L;
        ItemCreationDto itemDto = new ItemCreationDto();
        itemDto.setName("new name");
        itemDto.setDescription("new description");

        Item item = new Item();
        item.setId(itemId);
        item.setName("old name");
        item.setDescription("old description");
        item.setOwner(userId);

        ItemCreationDto itemDto1 = new ItemCreationDto();
        itemDto1.setId(itemId);
        itemDto1.setName("new name");
        itemDto1.setDescription("new description");

        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(item);
        when(modelMapper.map(item, ItemCreationDto.class)).thenReturn(itemDto1);

        ItemReplyDto updatedItem = itemService.updateItem(itemDto, itemId, userId);

        assertEquals(itemDto.getName(), updatedItem.getName());
        assertEquals(itemDto.getDescription(), updatedItem.getDescription());
        verify(itemRepository).save(item);
    }

    @Test
    void updateItem_WithInappropriateUser_ShouldThrowInappropriateUserException() {
        Long itemId = 1L;
        Long userId = 2L;
        ItemCreationDto itemDto = new ItemCreationDto();
        itemDto.setName("new name");
        itemDto.setDescription("new description");

        Item item = new Item();
        item.setId(itemId);
        item.setName("old name");
        item.setDescription("old description");
        item.setOwner(3L);

        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(InappropriateUserException.class, () -> itemService.updateItem(itemDto, itemId, userId));
    }
}