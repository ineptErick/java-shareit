package ru.practicum.shareit.user.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositories.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
// Так как ты познакомилась с транзакциями, хорошо было бы все сервисы сделать транзакционными
// Для этого необходимо навесить над ними аннотацию
// @Transactional(readOnly = true), что указывает на то, что фиксация изменений не будет производиться
// А над теми методами, где требуется фиксация в БД, необходимо использовать @Transactional
// - done
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ModelMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getUserDtoById(long userId) {
        return convertUserToDto(getUserById(userId));
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertUserToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        User user = convertDtoToUser(userDto);
        return convertUserToDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDto updateUser(UserDto userDto, long userId) {
        isExistUser(userId);
        // Действительно ли требуется производить дополнительный запрос для проверки наличия пользователя?
        // Ниже, вместо get, было бы удобно использовать orElseThrow
        // - done
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("There is no such a user"));

        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            // Хорошо было бы также проверить, что строка не пуста и не состоит только из пробелов
            // Сделать это удобно с помощью метода isBlank
            // - done
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            // Хорошо было бы также проверить, что строка не пуста и не состоит только из пробелов
            // Сделать это удобно с помощью метода isBlank
            // - done
            user.setEmail(userDto.getEmail());
        }

        return convertUserToDto(user);
        // Вызов метода save более не будет требоваться,
        // так как мы будем работать в рамках транзакции с полученным объектом класса-сущности из БД,
        // а значит изменения будут автоматически зафиксированы
        // - done
    }

    @Override
    @Transactional
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public void isExistUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found: " + userId);
        }
    }

    private User convertDtoToUser(UserDto userDto) {
        return mapper.map(userDto, User.class);
    }

    private UserDto convertUserToDto(User user) {
        return mapper.map(user, UserDto.class);
    }

    // (предложение) Дополнительный запрос к БД не требуется,
    // так как на уровне таблицы создан индекс для поддержания уникальности почты,
    // следовательно - при попытке записи - возникнет исключение,
    // которое можно обработать в одном из хендлеров и вернуть требуемый статус
    // - isUsedEmail удалила

}
