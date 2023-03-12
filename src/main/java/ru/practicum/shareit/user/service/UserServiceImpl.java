package ru.practicum.shareit.user.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.AlreadyUsedEmail;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ModelMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers().stream()
                .map(this::convertUserToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = convertDtoToUser(userDto);
        isEmailUsed(user.getEmail());
        return convertUserToDto(userRepository.createUser(user));
    }

    @Override
    public UserDto updateUser(UserDto userDto, int userId) {
        if (isUserExist(userId)) {
            User user = convertDtoToUser(userDto);
            isEmailUsed(user.getEmail(), userId);
            return convertUserToDto(userRepository.updateUser(user, userId));
        } else {
            throw new RuntimeException();
        }

    }

    @Override
    public void deleteUser(int userId) {
        userRepository.deleteUser(userId);
    }

    @Override
    public UserDto getUserById(int userUd) {
        return convertUserToDto(userRepository.getUserById(userUd));
    }

    @Override
    public boolean isUserExist(int userId) {
        return userRepository.getUserRepo().containsKey(userId);
    }

    private User convertDtoToUser(UserDto userDto) {
        return mapper.map(userDto, User.class);
    }

    private UserDto convertUserToDto(User user) {
        return mapper.map(user, UserDto.class);
    }

    private void isEmailUsed(String email) {
        userRepository.getUserRepo().values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .ifPresent(s -> {
                    throw new AlreadyUsedEmail(email);
                });
    }

    private void isEmailUsed(String email, int userId) {
        userRepository.getUserRepo().values().stream()
                .filter(user -> user.getEmail().equals(email) && user.getId() != userId)
                .findFirst()
                .ifPresent(s -> {
                    throw new AlreadyUsedEmail(email);
                });
    }
}
