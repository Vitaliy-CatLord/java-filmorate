package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RestControllerAdvice
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    private static final String SETTING_FRIENDS_PATH = "/{id}/friends/{friendId}";
    private static final String FRIENDS_LIST_PATH = "/{id}/friends";
    private static final String COMMON_FRIENDS_PATH = "/{id}/friends/common/{otherId}";

    @PostMapping
    public UserDto postUser(@Valid @RequestBody NewUserRequest newUser) {
        log.info("Выполнение запроса на создание пользователя {}", newUser.getName());
        return userService.createUser(newUser);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Выполнение запроса на получение всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        log.info("Выполнение запроса на получение пользователя с ID {}", id);
        return userService.getUserById(id);
    }

    @PutMapping
    public UserDto putUser(@Valid @RequestBody UpdateUserRequest updateUser) {
        log.info("Выполнение запроса на изменение пользователя с ID {}", updateUser.getId());
        return userService.updateUser(updateUser);
    }

    @PutMapping(SETTING_FRIENDS_PATH)
    public void makeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Выполнение запроса пользователя c ID {} на создание заявки на дружбу c {}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping(SETTING_FRIENDS_PATH)
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Выполнение запроса пользователя c ID {} на аннулирование дружбы c {}", id, friendId);
        userService.removeFriend(id, friendId);
    }

    @GetMapping(FRIENDS_LIST_PATH)
    public List<UserDto> getUserFriends(@PathVariable Long id) {
        log.info("Выполнение запроса на получение списка друзей пользователя с ID {}", id);
        return userService.getUserFriends(id);
    }

    @GetMapping(COMMON_FRIENDS_PATH)
    public List<UserDto> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Выполнение запроса на получение списка общих друзей пользователя с ID {}" +
                " и пользователя {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }
}
