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

    public Film(String name, String description, Instant releaseDate, Duration duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public Film(String name, String description, Instant releaseDate) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
    }

    public Film(String name, String description, Duration duration) {
        this.name = name;
        this.description = description;
        this.duration = duration;
    }

    public Film(String name, Instant releaseDate, Duration duration) {
        this.name = name;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public Film(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Film(String name, Instant releaseDate) {
        this.name = name;
        this.releaseDate = releaseDate;
    }

    public Film(String name, Duration duration) {
        this.name = name;
        this.duration = duration;
    }

    public Film(String name) {
        this.name = name;
    }

    public Film(Long id, String name, String description, Instant releaseDate, Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public Film(Long id, String name, String description, Instant releaseDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
    }

    public Film(Long id, String name, String description, Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.duration = duration;
    }

    public Film(Long id, String name, Instant releaseDate, Duration duration) {
        this.id = id;
        this.name = name;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public Film(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Film(Long id, String name, Instant releaseDate) {
        this.id = id;
        this.name = name;
        this.releaseDate = releaseDate;
    }

    public Film(Long id, String name, Duration duration) {
        this.id = id;
        this.name = name;
        this.duration = duration;
    }

    public Film(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
