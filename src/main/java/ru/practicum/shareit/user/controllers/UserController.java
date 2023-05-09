package ru.practicum.shareit.user.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.Create;
import ru.practicum.shareit.user.dto.Update;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.services.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable long id) {
        return userService.getUserDtoById(id);
    }

    @PostMapping()
    public UserDto createUser(@Validated(Create.class) @RequestBody UserDto userDto) {
        // Кстати, вместо @Valid можно использовать такую аннотацию @Validated(Create.class).
        // Таким образом мы указываем, какую группу аннотаций следует учитывать.
        // Create - это интерфейс, который ты создаешь самостоятельно,
        // он будет пустым и служит лишь для указания группы
        // Аналогично следует поступить при обновлении, но там будет аннотация @Validated(Update.class)
        // - поняла, но пока не стала реализовывать это в этом проекте
        return userService.createUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@Validated(Update.class) @RequestBody UserDto userDto, @PathVariable long id) {
        return userService.updateUser(userDto, id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
    }
}
