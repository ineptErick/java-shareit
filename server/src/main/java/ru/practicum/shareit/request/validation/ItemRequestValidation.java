package ru.practicum.shareit.request.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exeptions.ModelValidationException;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;

@Component("itemRequestValidation")
@Slf4j
public class ItemRequestValidation {
    @Autowired
    @Qualifier("dbItemRequestRepository")
    private ItemRequestRepository requestRepository;

    public void requestValidation(ItemRequest itemRequest) {
        if ((itemRequest.getDescription() == null) ||
        itemRequest.getDescription().isBlank()) {
            throw new ModelValidationException("Описание не может быть пустым.");
        }
    }

    public ItemRequest isPresent(Long requestId) {
        if (requestRepository.findById(requestId).isEmpty()) {
            log.error(String.format("Запрос с ID %s не найден.", requestId));
            throw new NotFoundException(String.format("Запрос с ID %d не найден.", requestId));
        }
        return requestRepository.getById(requestId);
    }
}
