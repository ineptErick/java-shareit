package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controllers.ItemController;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemReplyDto;
import ru.practicum.shareit.item.services.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({SpringExtension.class})
@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Long USER_ID = 1L;
    private static final Long ITEM_ID = 2L;

    @Test
    public void testGetItemById() throws Exception {
        ItemReplyDto itemDto = new ItemReplyDto();
        itemDto.setId(ITEM_ID);
        itemDto.setName("Test item");

        when(itemService.getItemDtoById(eq(ITEM_ID), eq(USER_ID))).thenReturn(itemDto);

        mockMvc.perform(get("/items/" + ITEM_ID).header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(ITEM_ID)))
                .andExpect(jsonPath("$.name", is("Test item")));
    }

    @Test
    public void testGetItems() throws Exception {
        ItemReplyDto item1 = new ItemReplyDto();
        item1.setId(ITEM_ID);
        item1.setName("Test item 1");

        ItemReplyDto item2 = new ItemReplyDto();
        item2.setId(ITEM_ID + 1L);
        item2.setName("Test item 2");

        List<ItemReplyDto> items = Arrays.asList(item1, item2);

        when(itemService.getItems(eq(USER_ID), eq(null), eq(null))).thenReturn(items);

        mockMvc.perform(get("/items").header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(ITEM_ID)))
                .andExpect(jsonPath("$[0].name", is("Test item 1")))
                .andExpect(jsonPath("$[1].id", is(ITEM_ID + 1L)))
                .andExpect(jsonPath("$[1].name", is("Test item 2")));
    }

    @Test
    public void testSearchItemsByText() throws Exception {
        ItemReplyDto item1 = new ItemReplyDto();
        item1.setId(ITEM_ID);
        item1.setName("Test item 1");

        ItemReplyDto item2 = new ItemReplyDto();
        item2.setId(ITEM_ID + 1L);
        item2.setName("Test item 2");

        List<ItemReplyDto> items = Arrays.asList(item1, item2);

        when(itemService.searchItemByText(anyString())).thenReturn(items);

        mockMvc.perform(get("/items/search").param("text", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is((ITEM_ID))))
                .andExpect(jsonPath("$[0].name", is("Test item 1")))
                .andExpect(jsonPath("$[1].id", is(ITEM_ID + 1L)))
                .andExpect(jsonPath("$[1].name", is("Test item 2")));
    }

    @Test
    public void testCreateItem_Success() throws Exception {
        ItemCreationDto itemDto = new ItemCreationDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Desc");
        itemDto.setAvailable(true);

        ItemReplyDto itemReplyDto = new ItemReplyDto();
        itemReplyDto.setName(itemDto.getName());
        itemReplyDto.setDescription(itemDto.getDescription());
        itemReplyDto.setAvailable(itemDto.getAvailable());

        // так как в пункт ниже нужно использовать просто айтем дто, то в первой части используем криэйшн
        // а во второй реплай
        // и я добавила копирование полей в реплай выше
        when(itemService.createItem(eq(itemDto), anyLong())).thenReturn(itemReplyDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andReturn();
    }

    @Test
    public void testCreateItem_InvalidItem() throws Exception {
        ItemCreationDto itemDto = new ItemCreationDto();
        itemDto.setName("");

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void testCreateComment_Success() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Test Comment");

        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText(commentDto.getText());
// здесь тоже самое сделала
        when(itemService.createComment(eq(commentRequestDto), anyLong(), anyLong())).thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andReturn();
    }

    @Test
    public void testUpdateItem_Success() throws Exception {
        ItemCreationDto itemDto = new ItemCreationDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");

        ItemReplyDto itemReplyDto = new ItemReplyDto();
        itemReplyDto.setId(itemDto.getId());
        itemReplyDto.setName(itemDto.getName());

        when(itemService.updateItem(eq(itemDto), anyLong(), anyLong())).thenReturn(itemReplyDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andReturn();
    }
}