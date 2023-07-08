package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserBookingDto;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    void toUserDto() {
        User user = new User();
        user.setId(0L);
        user.setName("User1");
        user.setEmail("user1@mail.ru");

        UserDto userDtoFromUser = UserMapper.INSTANT.toUserDto(user);

        Assertions.assertAll(
                () -> assertEquals(userDtoFromUser.getId(), user.getId()),
                () -> assertEquals(userDtoFromUser.getName(), user.getName()),
                () -> assertEquals(userDtoFromUser.getEmail(), user.getEmail())
        );
    }

    @Test
    void toUserBookingDto() {
        User user = new User();
        user.setId(0L);
        user.setName("User1");
        user.setEmail("user1@mail.ru");

        UserBookingDto userBookingDto = UserMapper.INSTANT.toUserBookingDto(user);

        assertEquals(userBookingDto.getId(), user.getId());
    }

    @Test
    void toUser() {
        UserDto userDto = new UserDto();
        userDto.setId(0L);
        userDto.setName("User1");
        userDto.setEmail("user1@mail.ru");

        User userFromDto = UserMapper.INSTANT.toUser(userDto);

        Assertions.assertAll(
                () -> assertEquals(userFromDto.getId(), userDto.getId()),
                () -> assertEquals(userFromDto.getName(), userDto.getName()),
                () -> assertEquals(userFromDto.getEmail(), userDto.getEmail())
        );
    }
}