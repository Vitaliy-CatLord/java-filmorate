package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
@EqualsAndHashCode(of = "id")
public class Film {

    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;

    public Film() {
    }

    public Film(String name, String description, LocalDate releaseDate, Integer duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public Film(String name, String description, LocalDate releaseDate) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
    }

    public Film(String name, String description, Integer duration) {
        this.name = name;
        this.description = description;
        this.duration = duration;
    }

    public Film(String name, LocalDate releaseDate, Integer duration) {
        this.name = name;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public Film(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Film(String name, LocalDate releaseDate) {
        this.name = name;
        this.releaseDate = releaseDate;
    }

    public Film(String name, int duration) {
        this.name = name;
        this.duration = duration;
    }

    public Film(String name) {
        this.name = name;
    }

    public Film(Long id, String name, String description, LocalDate releaseDate, Integer duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public Film(Long id, String name, String description, LocalDate releaseDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
    }

    public Film(Long id, String name, String description, Integer duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.duration = duration;
    }

    public Film(Long id, String name, LocalDate releaseDate, Integer duration) {
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

    public Film(Long id, String name, LocalDate releaseDate) {
        this.id = id;
        this.name = name;
        this.releaseDate = releaseDate;
    }

    public Film(Long id, String name, Integer duration) {
        this.id = id;
        this.name = name;
        this.duration = duration;
    }

    public Film(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
