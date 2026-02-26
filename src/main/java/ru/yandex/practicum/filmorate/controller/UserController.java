package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {
    private final Map<Long, User> usersBase = new HashMap<>();

    @PostMapping
    public User addUser (@RequestBody User newUser) {
        if(newUser.getEmail() == null || newUser.getEmail().isBlank() || newUser.getEmail().contains("@")) {
            throw new ValidationException("Неверно указан Емеил");
        }
        if(newUser.getLogin() == null || newUser.getLogin().isBlank() || newUser.getLogin().contains(" ")) {
            throw new ValidationException("Неверно указан логин");
        }
        if(newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        if(newUser.getBirthday().isAfter(Instant.now())) {
            throw new ValidationException("Неверно указана дата рождения");
        }

        newUser.setId(getNextId());
        usersBase.put(newUser.getId(), newUser);
        return newUser;
    }

    @PutMapping
    public User updateUser (@RequestBody User newUser) {
        if(newUser.getEmail() == null || newUser.getEmail().isBlank() || newUser.getEmail().contains("@")) {
            throw new ValidationException("Неверно указан Емеил");
        }
        if(newUser.getLogin() == null || newUser.getLogin().isBlank() || newUser.getLogin().contains(" ")) {
            throw new ValidationException("Неверно указан логин");
        }
        if(newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        if(newUser.getBirthday().isAfter(Instant.now())) {
            throw new ValidationException("Неверно указана дата рождения");
        }

        if(usersBase.containsKey(newUser.getId())) {
            User oldUser = usersBase.get(newUser.getId());
            oldUser = newUser;
            return oldUser;
        }
        throw new ValidationException("Пользователя с таким ID не существует");
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return usersBase.values();
    }


    private long getNextId() {
      long currentMaxId =  usersBase.keySet()
              .stream()
              .mapToLong(id -> id)
              .max()
              .orElse(0);
      return ++currentMaxId;
    }



}
