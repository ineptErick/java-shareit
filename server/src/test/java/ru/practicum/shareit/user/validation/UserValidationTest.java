package ru.practicum.shareit.user.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserValidationTest {


    @InjectMocks
    UserValidation userValidation;

    @Mock
    UserValidation userValidationMock;

    @Mock
    UserRepository userRepository;

    @Test
    void isEmailValid() {
        User user = new User();
        user.setId(0L);
        user.setName("User");
        user.setEmail("user@mail.ru");

        userValidationMock.isEmailValid(user);
        Mockito.verify(userValidationMock, Mockito.times(1)).isEmailValid(user);
    }

    @Test
    void isPresent_SUCCESS() {
        User user = new User();
        user.setId(0L);
        user.setName("User");
        user.setEmail("user@mail.ru");

       Mockito.when(userRepository.findById(ArgumentMatchers.any()))
                .thenReturn(Optional.ofNullable(user));

       userValidation.isPresent(user.getId());

        Mockito.verify(userRepository, Mockito.times(1)).getUserById(user.getId());

    }

    @Test
    void isPresent_FAIL() {
        User user = new User();
        user.setId(0L);
        user.setName("User");
        user.setEmail("user@mail.ru");

        doThrow(NotFoundException.class)
                .when(userRepository).findById(ArgumentMatchers.any());

        Assertions.assertThrows(NotFoundException.class,
                () -> userRepository.findById(ArgumentMatchers.any()));

        Mockito.verify(userRepository, Mockito.never()).getById(user.getId());

    }

    @Test
    void emailValidationForExistUser() {
        User user = new User();
        user.setId(0L);
        user.setName("User");
        user.setEmail("user@mail.ru");

        Mockito.when(userRepository.findByEmail(ArgumentMatchers.any()))
                .thenReturn(user);

        Mockito.when(userRepository.getById(ArgumentMatchers.any()))
                .thenReturn(user);

        userValidation.emailValidationForExistUser(user);

        Mockito.verify(userRepository, Mockito.times(1)).getById(user.getId());

    }
}