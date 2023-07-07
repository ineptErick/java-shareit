package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.validation.ItemRequestValidation;
import ru.practicum.shareit.user.validation.UserValidation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserValidation userValidation;
    private final ItemRequestValidation itemRequestValidation;

    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto saveRequest(ItemRequest itemRequest, Long userId) {
        userValidation.isPresent(userId);
        itemRequestValidation.requestValidation(itemRequest);
        itemRequest.setRequesterId(userId);
        itemRequest.setCreated(LocalDateTime.now());
        return ItemRequestMapper.INSTANT.toItemRequestDto(
                itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> findRequestsByOwnerId(Long ownerId) {
        userValidation.isPresent(ownerId);
        List<ItemRequestDto> requests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(ownerId)
                .stream()
                .map(ItemRequestMapper.INSTANT::toItemRequestDto)
                .collect(Collectors.toList());
        for (ItemRequestDto request: requests) {
                request.setItems(itemRepository.findAllByRequestId(request.getId()));
        }
        return requests;
    }

    @Override
    public List<ItemRequestDto> findAll(Long ownerId, Pageable pageable) {

        userValidation.isPresent(ownerId);
        Slice<ItemRequest> requestSlice = itemRequestRepository.findAllByRequesterIdNot(ownerId, pageable);
        while (!requestSlice.hasContent() && requestSlice.getNumber() > 0) {
            requestSlice = itemRequestRepository.findAllByRequesterIdNot(ownerId,
                    PageRequest.of(requestSlice.getNumber() - 1, requestSlice.getSize()));
        }
        List<ItemRequestDto> result = new ArrayList<>();
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        for (ItemRequest itemRequest: requestSlice) {
            itemRequestDto = ItemRequestMapper.INSTANT.toItemRequestDto(itemRequest);
            itemRequestDto.setItems(itemRepository.findAllByRequestId(itemRequestDto.getId()));
            result.add(itemRequestDto);
        }
        return result;
    }

    @Transactional
    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        userValidation.isPresent(userId);
        ItemRequestDto result = ItemRequestMapper.INSTANT.toItemRequestDto(itemRequestValidation.isPresent(requestId));
        result.setItems(itemRepository.findAllByRequestId(result.getId()));
        return result;
    }
}
