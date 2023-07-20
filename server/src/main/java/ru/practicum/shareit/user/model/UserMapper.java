package ru.practicum.shareit.user.model;

import ru.practicum.shareit.user.dto.UserBookingDto;
import ru.practicum.shareit.user.dto.UserDto;

public enum UserMapper {
    INSTANT;

    public UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public UserBookingDto toUserBookingDto(User user) {
        UserBookingDto userBookingDto = new UserBookingDto();
        userBookingDto.setId(user.getId());
        return userBookingDto;
    }

    public User toUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }
}
