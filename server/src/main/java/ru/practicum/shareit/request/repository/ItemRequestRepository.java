package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Component("dbItemRequestRepository")
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(Long requesterId);

    Slice<ItemRequest> findAllByRequesterIdNot(Long userId, Pageable pageable);

    ItemRequest getItemRequestById(Long itemRequestId);

}
