package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.Instant;

@Data
@EqualsAndHashCode(of = {"id", "email"})
@ToString
public class User {

    long id;
    String email;
    String login;
    String name;
    Instant birthday;
}
