package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> usersStorage = new HashMap<>();
    private final Logger log = LoggerFactory.getLogger(UserController.class);

    private static final LocalDate MIN_TIME_OF_BIRTHDAY = LocalDate.of(1909, 8, 21);

    @PostMapping
    public User postUser(@RequestBody User newUser) throws ValidationException {
        validateUser(newUser);
        isEmailEmployed(newUser);
        newUser.setId(getNextId());
        usersStorage.put(newUser.getId(), newUser);
        log.info("Создан новый юзер {}", newUser);
        return newUser;
    }

    @PutMapping
    public User putUser(@RequestBody User newUser) throws ValidationException {
        validateUser(newUser);
        return setOldUser(newUser);
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

    private void validateUser(User newUser) throws ValidationException, DuplicatedDataException {
        //mail
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
        if (newUser.getBirthday() != null && (newUser.getBirthday().isAfter(LocalDate.now())
                || newUser.getBirthday().isBefore(MIN_TIME_OF_BIRTHDAY))) {
            String message = "Неверно указана дата рождения";
            log.warn(message);
            throw new ValidationException(message);
        }

    }

    private User setOldUser(User newUser) {

        if (newUser.getId() == null) {
            String message = "Идентификатор должен быть указан";
            log.warn(message);
            throw new ValidationException(message);
        }
        //верификация для изменения учетки по совпадению тройки id-mail-login
        if (usersStorage.containsKey(newUser.getId())) {
            User oldUser = usersStorage.get(newUser.getId());
//            if (oldUser.getEmail().equals(newUser.getEmail())
//                    && oldUser.getLogin().equals(newUser.getLogin())) {
            if (!newUser.getEmail().isBlank()) {
                oldUser.setEmail(newUser.getEmail());
            }
            if (newUser.getName() != null && !newUser.getName().isBlank()) {
                oldUser.setName(newUser.getName());
            }
            if (newUser.getLogin() != null && !newUser.getLogin().isBlank()) {
                oldUser.setLogin(newUser.getLogin());
            }
            if (newUser.getBirthday() != null && newUser.getBirthday().isAfter(MIN_TIME_OF_BIRTHDAY)
                    && newUser.getBirthday().isBefore(LocalDate.now())) {
                oldUser.setBirthday(newUser.getBirthday());
            }
            log.info("Изменен пользователь. Новые данные: {}", oldUser);
            return oldUser;
//            } else {
//                String message = "Емаил и/или логин не соответствует ID";
//                log.warn(message);
//                throw new DuplicatedDataException(message);
//            }
        } else {
            String message = "Пользователя с Идентификатором " + newUser.getId() + " не существует";
            log.warn(message);
            throw new ValidationException(message);
        }


    }

    private void isEmailEmployed(User newUser) {
        if (usersStorage.values().stream()
                .anyMatch(oldUser -> newUser.getEmail().equals(oldUser.getEmail()))) {
            String message = "Емеил " + newUser.getEmail() + " занят";
            log.warn(message);
            throw new DuplicatedDataException(message);
        }

    }

    public void cleanStorage() {
        usersStorage.clear();
    }
}
