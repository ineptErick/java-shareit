package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserGatewayDto;
import ru.practicum.shareit.user.dto.UserGatewayForUpdateDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> addUser(
            @RequestBody @Valid UserGatewayDto user) {
        log.info("Создание пользователя с именем: {} и почтой: {}.", user.getName(), hideEmail(user));
        return userClient.addUser(user);
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Запрос всех пользователей.");
        return userClient.getUsers();
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(
            @RequestBody @Valid UserGatewayForUpdateDto userDto,
            @Valid @Positive(message = "ID пользователя должен быть > 0.")
            @PathVariable
            Long userId) {
        log.info("Обновление пользователя с userId={}.", userId);
        return userClient.updateUser(userDto, userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(
            @Valid @Positive(message = "ID пользователя должен быть > 0.")
            @PathVariable
            Long userId) {
        log.info("Запрос на получение пользователя с userId={}.", userId);
        return userClient.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(
            @Valid @Positive(message = "ID пользователя должен быть > 0.")
            @PathVariable
            Long userId) {
        log.info("Удаление пользователя с userId={}.", userId);
        return userClient.deleteUser(userId);
    }

    private String hideEmail(UserGatewayDto user) {
        StringBuilder email = new StringBuilder();
        String[] parts = user.getEmail().split("@");
        int loginSize = parts[0].length();
        if (loginSize > 5) {
            email.append(parts[0], 0, 3);
            for (int i = 3; i < parts[0].length() - 1; i++) {
                email.append("*");
            }
            email.append(parts[0], parts[0].length() - 1, parts[0].length());
        } else if (loginSize > 2) {
            email.append(parts[0].substring(0,1));
            for (int i = 1; i < parts[0].length() - 1; i++) {
                email.append("*");
            }
            email.append(parts[0], parts[0].length() - 1, parts[0].length());
        } else {
            email.append(parts[0]);
        }
        email.append("@").append(parts[1]);
        return email.toString();
    }
}