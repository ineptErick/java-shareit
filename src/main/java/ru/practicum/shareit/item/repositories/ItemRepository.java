package ru.practicum.shareit.item.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = "SELECT i FROM Item i LEFT JOIN FETCH i.comments c WHERE i.owner = :id")
    List<Item> findAllByOwner(@Param("id") Long ownerId, Sort sort);
    Slice<Item> findAllByOwner(Long ownerId, Pageable pageable);

    @Query(value = "SELECT * FROM items WHERE (name ILIKE CONCAT('%',?1,'%') OR description ILIKE CONCAT('%',?1,'%')) AND " +
            "is_available = TRUE", nativeQuery = true)
    List<Item> searchItemByText(String text);

}
