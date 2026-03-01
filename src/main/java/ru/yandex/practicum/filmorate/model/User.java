package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = {"id", "email"})
public class User {

    Long id;
    String email;
    String login;
    String name;
    LocalDate birthday;

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
