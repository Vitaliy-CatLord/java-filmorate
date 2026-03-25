package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RestControllerAdvice
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Выполнение запроса на получение всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        log.info("Выполнение запроса на получение пользователя с ID {}", id);
        return userService.getUserById(id);
    }

    @PostMapping
    public User postUser(@RequestBody User newUser) {
        log.info("Выполнение запроса на создание пользователя с ID {}", newUser);
        return userService.createUser(newUser);
    }

    @PutMapping
    public User putUser(@RequestBody User newUser) {
        log.info("Выполнение запроса на изменение пользователя с ID {}", newUser.getId());
        return userService.updateUser(newUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void makeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Выполнение запроса пользователя c ID {} на дружбу c {}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Выполнение запроса пользователя c ID {} на аннулирование дружбы c {}", id, friendId);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getUserFriends(@PathVariable Long id) {
        log.info("Выполнение запроса на получение списка друзей пользователя с ID {}", id);
        return userService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getUserFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Выполнение запроса на получение списка общих друзей пользователя с ID {}" +
                " и пользователя {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }
}
