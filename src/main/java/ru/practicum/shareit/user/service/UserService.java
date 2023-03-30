package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, int id);

    UserDto getUserDtoById(int id);

    void deleteUser(int userId);

    void isUserExist(int userId);

    User getUserById(int userId);
}
