package ru.practicum.shareit.user.cntroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;
    @MockBean
    UserService userService;

    @Test
    void addUser() throws Exception {
        long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setName("User1");
        user.setEmail("user1@mail.ru");

        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setName("User1");
        userDto.setEmail("user1@mail.ru");

        Mockito.when(userService.saveUser(user)).thenReturn(userDto);
        String result = mockMvc.perform(post("/users", user)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(user), result);
    }

    @Test
    void updateUser() throws Exception {
        long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setName("User1");
        userDto.setEmail("user1@mail.ru");

        Mockito.when(userService.updateUser(userDto)).thenReturn(userDto);
        String result = mockMvc.perform(MockMvcRequestBuilders.patch("/users/{id}", userId, userDto)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(userDto), result);
    }

    @Test
    void getUsers() throws Exception {
        List<UserDto> userDtos = List.of();

        Mockito.when(userService.getAllUsers()).thenReturn(userDtos);

        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(userService).getAllUsers();
    }

    @Test
    void getUser() throws Exception {
        long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setName("User1");
        userDto.setEmail("user1@mail.ru");

        Mockito.when(userService.getUserById(userId)).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", userId))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(userService).getUserById(userId);
    }

    @Test
    void deleteUser() throws Exception {
        long userId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", userId))
                .andExpect(MockMvcResultMatchers.status().isOk());
        Mockito.verify(userService).deleteUser(userId);
    }

}