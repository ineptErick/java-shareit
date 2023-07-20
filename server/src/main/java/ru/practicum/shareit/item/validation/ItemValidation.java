package ru.practicum.shareit.item.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exeptions.BookingUnavailableException;
import ru.practicum.shareit.exeptions.ModelValidationException;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

@Component("itemValidation")
@Slf4j
public class ItemValidation {

    @Autowired
    @Qualifier("dbItemRepository")
    private ItemRepository itemRepository;

    public void itemValidation(Item item) {
        if (item.getName().isBlank()) {
            log.error(String.format("Объект не создан. Отсутствует название."));
            throw new ModelValidationException("Имя не может быть пустым.");
        }
        if (item.getDescription() == null) {
            log.error(String.format("Объект не создан. Отсутствует описание."));
            throw new ModelValidationException("Описание не может быть пустым.");
        }
        if (item.getAvailable() == null) {
            log.error(String.format("Объект не создан. Для новых объектов статус доступа должен быть 'true'.",
                    item.getId()));
            throw new ModelValidationException("Статус объекта должен быть 'Доступно' при создании.");
        }
    }

    public Item isPresent(Long itemId) {
        if (itemRepository.findById(itemId).isEmpty()) {
            log.error(String.format("Объект с ID %s не найден.", itemId));
            throw new NotFoundException(String.format("Объект с ID %d не найден.", itemId));
        }
        return itemRepository.getItemById(itemId);
    }

    public void isAvailable(Long itemId) {
        if (!itemRepository.findById(itemId).get().getAvailable()) {
            log.error(String.format("Объект с ID %s уже занят.", itemId));
            throw new BookingUnavailableException(String.format("Объект с ID %s уже занят.", itemId));
        }
    }
}
