package ru.yandex.practicum.filmorate.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Duration;
import java.time.Instant;

/**
 * Film.
 */
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
public class Film {

    long id;
    String name;
    String description;
    Instant releaseDate;
    Duration duration;
}
