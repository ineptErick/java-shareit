package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exeptions.ModelConflictException;
import ru.practicum.shareit.exeptions.ModelValidationException;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.validation.UserValidation;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    UserValidation userValidation;

    @Captor
    ArgumentCaptor<User> userArgumentCaptor;


    @Test
    void getAllUsers_whenNoUsers_thenEmptyList() {
        Mockito.when(userRepository.findAll())
                .thenReturn(new ArrayList<User>());

        List<UserDto> emptyListOfUsers = userService.getAllUsers();

        Mockito.verify(userRepository, Mockito.times(1)).findAll();
        Assertions.assertEquals(emptyListOfUsers.size(), 0);
    }

    @Test
    void getAllUsers_whenUsersAdded_thenListOfUsers() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            User user = new User();
            user.setId(((long) i));
            user.setName("User" + i);
            user.setEmail("user" + i + "@mail.ru");
            users.add(user);
        }
        Mockito.when(userRepository.findAll())
                .thenReturn(users);

        List<User> listOfUsers = userRepository.findAll();

        Mockito.verify(userRepository, Mockito.times(1)).findAll();
        Assertions.assertEquals(listOfUsers.size(), 5);
    }

    @Test
    void saveUser_whenUserEmailValid_thenSavedUser() {
        User userToSave = new User();
        Mockito.when(userRepository.save(new User()))
                .thenReturn(new User());

        UserDto actualUser = userService.saveUser(userToSave);

        assertEquals(userToSave, UserMapper.INSTANT.toUser(actualUser));
        Mockito.verify(userRepository).save(userToSave);
    }

    @Test
    void saveUser_whenUserEmailIsBusy_thenNotSavedUser() {
        User userToSave = new User();
        doThrow(ModelConflictException.class)
                .when(userValidation).isEmailValid(userToSave);

        Assertions.assertThrows(ModelConflictException.class,
                () -> userService.saveUser(userToSave));

        Mockito.verify(userRepository, Mockito.never()).save(userToSave);
    }

    @Test
    void saveUser_whenUserEmailNotValid_thenNotSavedUser() {
        User userToSave = new User();
        doThrow(ModelValidationException.class)
                .when(userValidation).isEmailValid(userToSave);

        Assertions.assertThrows(ModelValidationException.class,
                () -> userService.saveUser(userToSave));

        Mockito.verify(userRepository, Mockito.never()).save(userToSave);
    }

    @Test
    void updateUser_whenUserFoundAndEmailAvailable_thenUpdateUser() {
        Long userId = 0L;

        User oldUser = new User();
        oldUser.setId(userId);
        oldUser.setName("Name");
        oldUser.setEmail("user@mail.ru");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("NameUpdated");
        updatedUser.setEmail("userUpdated@mail.ru");


        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setId(userId);
        updatedUserDto.setName("NameUpdated");
        updatedUserDto.setEmail("userUpdated@mail.ru");
        Mockito.when(userRepository.getById(userId))
                .thenReturn(oldUser);
        Mockito.when(userRepository.save(ArgumentMatchers.any()))
                .thenReturn(updatedUser);

        userService.updateUser(updatedUserDto);

        Mockito.verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();

        assertEquals("NameUpdated", savedUser.getName());
        assertEquals("userUpdated@mail.ru", savedUser.getEmail());
    }

    @Test
    void updateUser_whenUserFoundAndEmailAvailable_thenUpdateUserEmail() {
        Long userId = 0L;

        User oldUser = new User();
        oldUser.setId(userId);
        oldUser.setName("Name");
        oldUser.setEmail("user@mail.ru");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("Name");
        updatedUser.setEmail("userUpdated@mail.ru");


        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setId(userId);
        updatedUserDto.setEmail("userUpdated@mail.ru");

        Mockito.when(userRepository.getById(userId))
                .thenReturn(oldUser);
        Mockito.when(userRepository.save(ArgumentMatchers.any()))
                .thenReturn(updatedUser);

        userService.updateUser(updatedUserDto);

        Mockito.verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();

        assertEquals("Name", savedUser.getName());
        assertEquals("userUpdated@mail.ru", savedUser.getEmail());
    }

    @Test
    void updateUser_whenUserFoundAndEmailAvailable_thenUpdateUserName() {
        Long userId = 0L;

        User oldUser = new User();
        oldUser.setId(userId);
        oldUser.setName("Name");
        oldUser.setEmail("user@mail.ru");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("NameUpdated");
        updatedUser.setEmail("user@mail.ru");


        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setId(userId);
        updatedUserDto.setName("NameUpdated");

        Mockito.when(userRepository.getById(userId))
                .thenReturn(oldUser);
        Mockito.when(userRepository.save(ArgumentMatchers.any()))
                .thenReturn(updatedUser);

        userService.updateUser(updatedUserDto);

        Mockito.verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();

        assertEquals("NameUpdated", savedUser.getName());
        assertEquals("user@mail.ru", savedUser.getEmail());
    }

    @Test
    void updateUser_whenUserNotFound_thenNotUpdateUser() {
        Long userId = 0L;

        UserDto updatedUser = new UserDto();
        updatedUser.setId(userId);
        updatedUser.setName("Name");
        updatedUser.setEmail("user@mail.ru");

        Mockito.when(userValidation.isPresent(userId))
                .thenThrow(NotFoundException.class);

        Assertions.assertThrows(NotFoundException.class,
                () -> userService.updateUser(updatedUser));

        Mockito.verify(userRepository, Mockito.never()).save(UserMapper.INSTANT.toUser(updatedUser));
    }

    @Test
    void updateUser_whenUserFoundAndEmailNotValid_thenNotUpdateUser() {
        User userToSave = new User();

        doThrow(ModelValidationException.class)
                .when(userValidation).emailValidationForExistUser(userToSave);

        Assertions.assertThrows(ModelValidationException.class,
                () -> userService.updateUser(UserMapper.INSTANT.toUserDto(userToSave)));

        Mockito.verify(userRepository, Mockito.never()).save(userToSave);
    }

    @Test
    void updateUser_whenUserFoundAndEmailIsBusy_thenNotUpdateUser() {
        User userToSave = new User();

        doThrow(ModelConflictException.class)
                .when(userValidation).emailValidationForExistUser(userToSave);

        Assertions.assertThrows(ModelConflictException.class,
                () -> {
                    userService.updateUser(UserMapper.INSTANT.toUserDto(userToSave)); });

        Mockito.verify(userRepository, Mockito.never()).save(userToSave);
    }

    @Test
    void getUserById_whenUserFound_thenReturnUser() {
        Long userId = 0L;
        UserDto exceptedUser = new UserDto();
        exceptedUser.setId(userId);
        exceptedUser.setName("Name");
        exceptedUser.setEmail("user@mail.ru");

        User user = new User();
        user.setId(userId);
        user.setName("Name");
        user.setEmail("user@mail.ru");


        Mockito.when(userValidation.isPresent(ArgumentMatchers.any()))
                .thenReturn(user);

        UserDto actualUser = userService.getUserById(userId);
        assertEquals(exceptedUser, actualUser);
    }

    @Test
    void getUserById_whenUserNotFound_thenUserNotFoundException() {
        Long userId = 0L;
        Mockito.when(userValidation.isPresent(userId))
                .thenThrow(NotFoundException.class);

        Assertions.assertThrows(NotFoundException.class,
                () -> userService.getUserById(userId));
    }

    @Test
    void deleteUser_whenUserIsPresent_thenDeleteUser() {
        Long userId = 0L;

        userService.deleteUser(userId);

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(userId);
    }

    @Test
    void deleteUser_whenUserIsNotPresent_thenThrowNotFoundException() {
        Long userId = 0L;

        doThrow(NotFoundException.class)
                .when(userValidation).isPresent(userId);

        Assertions.assertThrows(NotFoundException.class,
                () ->  userService.deleteUser(userId));

        Mockito.verify(userRepository, Mockito.never()).deleteById(userId);
    }

}