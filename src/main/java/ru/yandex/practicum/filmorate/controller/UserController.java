package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> usersStorage = new HashMap<>();
    private final static Logger log = LoggerFactory.getLogger(UserController.class);

    private static final Instant MIN_TIME_OF_BIRTHDAY = LocalDateTime.of(1909, 8, 21, 0, 0)
            .atZone(ZoneId.of("Europe/Paris"))
            .toInstant();

    @PostMapping
    public User postUser(@RequestBody User newUser) throws ValidationException {
        //mail
        if (newUser.getEmail() == null || newUser.getEmail().isBlank()) {
            String message = "Неверно указан Емеил";
            log.warn(message);
            throw new ValidationException(message);
        }
        if (!newUser.getEmail().contains("@")) {
            String message = "Емаил должен содержать @";
            log.warn(message);
            throw new ValidationException(message);
        }
        if (isEmailEmployed(newUser)) {
            String message = "Данный емеил занят";
            log.warn(message);
            throw new DuplicatedDataException(message);
        }
        //login
        if (newUser.getLogin() == null || newUser.getLogin().isBlank()) {
            String message = "Неверно указан логин";
            log.warn(message);
            throw new ValidationException(message);
        }
        if (newUser.getLogin().contains(" ")) {
            String message = "Логин не должен содержать пробелов";
            log.warn(message);
            throw new ValidationException(message);
        }
        //name
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        //birthday
        if (newUser.getBirthday() != null && (newUser.getBirthday().isAfter(Instant.now())
                || newUser.getBirthday().isBefore(MIN_TIME_OF_BIRTHDAY))) {
            String message = "Неверно указана дата рождения";
            log.warn(message);
            throw new ValidationException(message);
        }


        newUser.setId(getNextId());
        usersStorage.put(newUser.getId(), newUser);
        log.info("Создан новый юзер {}", newUser);
        return newUser;
    }

    @PutMapping
    public User putUser(@RequestBody User newUser) throws ValidationException {

        if (newUser.getId() == null) {
            String message = "Id должен быть указан";
            log.warn(message);
            throw new ValidationException(message);
        }
        //верификация только по знанию id и email
        // (странно по одному айди человека верифицировать)
        // -значит оба неизменяемы
        if (usersStorage.containsKey(newUser.getId())) {
            if (newUser.getEmail() == null || newUser.getEmail().isBlank()) {
                String message = "Емеил должен быть заполнен";
                log.warn(message);
                throw new ValidationException(message);
            }
            if (!newUser.getEmail().contains("@")) {
                String message = "Емаил должен содержать @";
                log.warn(message);
                throw new ValidationException(message);
            }
            User oldUser = usersStorage.get(newUser.getId());

            if (oldUser.getEmail().equals(newUser.getEmail())) {
                if (!newUser.getEmail().isBlank()) {
                    oldUser.setEmail(newUser.getEmail());
                }
                if (newUser.getName() != null && !newUser.getName().isBlank()) {
                    oldUser.setName(newUser.getName());
                }
                if (newUser.getLogin() != null && !newUser.getLogin().isBlank()) {
                    oldUser.setLogin(newUser.getLogin());
                }
                if (newUser.getBirthday() != null && newUser.getBirthday().isBefore(MIN_TIME_OF_BIRTHDAY)) {
                    oldUser.setBirthday(newUser.getBirthday());
                }
                log.info("Изменен пользователь. Новые данные: {}", oldUser);
                return oldUser;
            } else {
                String message = "Емаил не соответствует ID";
                log.warn(message);
                throw new DuplicatedDataException(message);
            }
        } else {
            String message = "Пользователя с таким ID не существует";
            log.warn(message);
            throw new ValidationException(message);
        }
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return usersStorage.values();
    }

    private long getNextId() {
        long currentMaxId = usersStorage.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private boolean isEmailEmployed(User newUser) {
        return usersStorage.values().stream()
                .anyMatch(oldUser -> newUser.getEmail().equals(oldUser.getEmail()));

    }

    public void cleanStorage() {
        usersStorage.clear();
    }
}
