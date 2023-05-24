package ru.practicum.shareit.request.services;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createRequest(ItemRequestDto requestDto, Long userId);
    ItemRequestDto getRequestById(Long requestId, Long userId);
    List<ItemRequestDto> getOwnerRequests(Long ownerId);
    List<ItemRequestDto> getUserRequests(Long userId, Integer from, Integer size);
}