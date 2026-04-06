package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.model.enums.FriendshipStatus;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"id", "email"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    Long id;
    String email;
    String login;
    String name;
    LocalDate birthday;
    Map<Long, FriendshipStatus> friendsList = new HashMap<>();

    public User() {
    }

    public User(String email, String login) {
        this.email = email;
        this.login = login;
    }

    public User(String email, String login, String name) {
        this.email = email;
        this.login = login;
        this.name = name;
    }

    public User(String email, String login, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.birthday = birthday;
    }

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}
