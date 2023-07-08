package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Component("dbItemRepository")
public interface ItemRepository extends JpaRepository<Item, Long> {

    Slice<Item> getAllByOwnerOrderById(Long id, Pageable pageable);

    Slice<Item> searchByNameOrDescriptionContainingIgnoreCase(String name, String description, Pageable page);

    List<Item> findAllByRequestId(Long requestId, Pageable page);

    List<Item> findAllByRequestId(Long requestId);

    Item getItemById(Long itemId);
}