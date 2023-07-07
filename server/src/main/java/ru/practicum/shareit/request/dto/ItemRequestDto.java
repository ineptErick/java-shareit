package ru.practicum.shareit.request.dto;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class ItemRequestDto {
    private Long id;
    private String description;
    private Long requestorId;
    private LocalDateTime created;
    private List<Item> items = new ArrayList<>();
}
