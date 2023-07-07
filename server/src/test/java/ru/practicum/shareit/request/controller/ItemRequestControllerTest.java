package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ItemRequestService requestService;

    @Test
    void addRequest() throws  Exception {
        Long userId = 0L;
        ItemRequest itemRequest = new ItemRequest();
        ItemRequestDto itemRequestDto = new ItemRequestDto();

        Mockito.when(requestService.saveRequest(itemRequest, userId)).thenReturn(itemRequestDto);
        String result = mockMvc.perform(MockMvcRequestBuilders.post("/requests", itemRequest, userId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(itemRequestDto), result);
    }

    @Test
    void getUserRequests() throws Exception {
        Long userId = 0L;
        List<ItemRequestDto> itemRequestDtoList =  List.of();

        Mockito.when(requestService.findRequestsByOwnerId(userId)).thenReturn(itemRequestDtoList);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(requestService).findRequestsByOwnerId(userId);
    }

    @Test
    void getAllRequests() throws Exception {
        Long userId = 0L;
        List<ItemRequestDto> itemRequestDtoList =  List.of();

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "created"));
        Mockito.when(requestService.findAll(userId, pageRequest)).thenReturn(itemRequestDtoList);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("size", "10")
                        .param("from", "0"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(requestService).findAll(userId, pageRequest);
    }

    @Test
    void getRequestById() throws Exception {
        Long itemRequestId = 0L;
        Long userId = 0L;
        ItemRequestDto itemRequestDto = new ItemRequestDto();

        Mockito.when(requestService.getRequestById(userId, itemRequestId)).thenReturn(itemRequestDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/{requestId}", itemRequestId, userId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(requestService).getRequestById(itemRequestId, userId);
    }
}