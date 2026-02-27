package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.Duration;
import java.time.Instant;

/**
 * Film.
 */
@Data
@EqualsAndHashCode(of = "id")
public class Film {

    Long id;
    String name;
    String description;
    Instant releaseDate;
    Duration duration;
}
