package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.validation.UserValidation;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserValidation userValidation;

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Получен список пользователей.");
        return userRepository.findAll().stream()
                .map(UserMapper.INSTANT::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto saveUser(User user) {
        userValidation.isEmailValid(user);
        log.info(String.format("Пользователь с ID %s успешно создан.", user.getId()));
        return UserMapper.INSTANT.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        userValidation.isPresent(userDto.getId());
        userValidation.emailValidationForExistUser(UserMapper.INSTANT.toUser(userDto));
        User userForUpdate = userRepository.getById(userDto.getId());
        if (userDto.getName() != null) {
            userForUpdate.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            userForUpdate.setEmail(userDto.getEmail());
        }
        log.info(String.format("Пользователь с ID %s успешно обновлён.", userForUpdate.getId()));
        return UserMapper.INSTANT.toUserDto(userRepository.save(userForUpdate));
    }

    @Override
    public UserDto getUserById(Long userId) {
        return UserMapper.INSTANT.toUserDto(userValidation.isPresent(userId));
    }

    @Override
    public void deleteUser(Long userId) {
        userValidation.isPresent(userId);
        userRepository.deleteById(userId);
        log.info(String.format("Пользователь с ID %s успешно удалён.", userId));
    }
}