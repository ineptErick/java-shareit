package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto saveRequest(ItemRequest request, Long userId);

    List<ItemRequestDto> findRequestsByOwnerId(Long ownerId);

    List<ItemRequestDto> findAll(Long ownerId, Pageable pageable);

    ItemRequestDto getRequestById(Long userId, Long requestId);
}