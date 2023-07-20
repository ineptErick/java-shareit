package ru.practicum.shareit.request.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {

    @Test
    void toItemRequestDto() {

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(0L);
        itemRequest.setRequesterId(1L);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("Item Request");

        List<ItemDto> itemDtos = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            ItemDto itemDto = new ItemDto();
            itemDto.setId((long) i);
            itemDto.setName("ItemDto " + i);
            itemDto.setRequestId((long) i);
            itemDto.setAvailable(true);
            itemDto.setDescription("Description " + i);
            itemDto.setLastBooking(new BookingInfoDto((long) i, (long) i));
            itemDto.setNextBooking(new BookingInfoDto((long) i + 1, (long) i + 1));
            itemDto.setLastBookingDate(LocalDateTime.now());
            itemDto.setNextBookingDate(LocalDateTime.now().plusDays(1));
            itemDto.setComments(new ArrayList<>());

            itemDtos.add(itemDto);
        }

        ItemRequestDto itemRequestDto = ItemRequestMapper.INSTANT.toItemRequestDto(itemRequest);

        Assertions.assertAll(
                () -> assertEquals(itemRequestDto.getId(), itemRequest.getId()),
                () -> assertEquals(itemRequestDto.getCreated(), itemRequest.getCreated()),
                () -> assertEquals(itemRequestDto.getRequestorId(), itemRequest.getRequesterId()),
                () -> assertEquals(itemRequestDto.getDescription(), itemRequest.getDescription())
        );
    }

}