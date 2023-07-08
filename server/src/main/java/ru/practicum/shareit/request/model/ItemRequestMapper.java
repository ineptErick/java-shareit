package ru.practicum.shareit.request.model;

import ru.practicum.shareit.request.dto.ItemRequestDto;


public enum ItemRequestMapper {
    INSTANT;

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setRequestorId(itemRequest.getRequesterId());
        itemRequestDto.setCreated(itemRequest.getCreated());
        return itemRequestDto;
    }

}
