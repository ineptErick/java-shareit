package ru.practicum.shareit.user.services;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, Long id);

    void deleteUser(Long userId);

    UserDto getUserDtoById(Long id);

    void isExistUser(Long userId);

    User getUserById(Long userId);
}
