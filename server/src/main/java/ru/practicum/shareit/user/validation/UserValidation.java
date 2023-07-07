package ru.practicum.shareit.user.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exeptions.ModelConflictException;
import ru.practicum.shareit.exeptions.ModelValidationException;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.regex.Pattern;

@Component("userValidation")
@Slf4j
public class UserValidation {

    @Autowired
    @Qualifier("dbUserRepository")
    private UserRepository userRepository;

    private Pattern emailPattern = Pattern.compile("^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@" +
            "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

    private boolean validateEmail(final String hex) {
        return emailPattern.matcher(hex).matches();
    }

    public void isEmailValid(User user) {
        if (!validateEmail(user.getEmail())) {
            log.error(String.format("Пользователь не создан. Ошибка в адресе почты: %s.", user.getEmail()));
            throw new ModelValidationException(String.format("Почтовый адрес '%s' не может быть использован.",
                    user.getEmail()));
        }
    }

    public User isPresent(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            log.error(String.format("Пользователь с ID %s не существует.", userId));
            throw new NotFoundException(String.format("Пользователь с ID %d не найден.", userId));
        }
        return userRepository.getUserById(userId);
    }

    public void emailValidationForExistUser(User user) {
        if (user.getEmail() != null) {
            isEmailValid(user);
            if (userRepository.findByEmail(user.getEmail()) != null) {
                User userForUpdate = userRepository.getById(user.getId());
                if (!userForUpdate.getEmail().equals(user.getEmail())) {
                    isEmailBuse(user);
                }
            }
        }
    }

    private void isEmailBuse(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            log.error(String.format("Пользователь не создан. Почта %s уже занята.", user.getEmail()));
            throw new ModelConflictException(String.format("Почтовый адрес '%s' уже занят.",
                    user.getEmail()));
        }
    }
}
