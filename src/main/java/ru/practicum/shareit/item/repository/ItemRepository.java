package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.InappropriateUser;
import ru.practicum.shareit.item.model.Item;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemRepository {
    private int countItemId = 0;

    private Map<Integer, Item> itemRepo = new HashMap<>();

    public Map<Integer, Item> getItemRepo() {
        return itemRepo;
    }

    public Item createItem(Item item, int userId) {
        item.setId(++countItemId);
        item.setOwner(userId);
        itemRepo.put(countItemId, item);
        return item;
    }

    public void deleteItem(long userId) {
        itemRepo.remove(userId);
    }

    public Item updateItem(Item item, int itemId, int userId) {
        Item updatedItem = itemRepo.get(itemId);
        if (updatedItem.getOwner() != userId) {
            throw new InappropriateUser("Item already has a different user" + userId);
        }
        if (item.getName() != null) {
            updatedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updatedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }
        itemRepo.put(itemId, updatedItem);
        return updatedItem;
    }

    public List<Item> getItems(long userId) {
        return itemRepo.values().stream()
                .filter(item -> item.getOwner() == userId)
                .collect(Collectors.toList());
    }

    public Item getItemById(long itemId) {
        return itemRepo.get(itemId);
    }

    public List<Item> searchItemByText(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        } else {
            return itemRepo.values().stream()
                    .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                            || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                    .filter(item -> item.getAvailable().equals(true))
                    .collect(Collectors.toList());
        }
    }
}
