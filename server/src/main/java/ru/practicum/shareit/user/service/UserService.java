package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();

    UserDto saveUser(User user);

    UserDto updateUser(UserDto userDto);

    UserDto getUserById(Long userId);

    void deleteUser(Long userId);
}
