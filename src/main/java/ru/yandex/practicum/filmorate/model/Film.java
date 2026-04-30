package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = "id")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {

    Long id;
    String name;
    String description;
    LocalDate releaseDate;
    Integer duration;
    Set<Long> likesUserId = new HashSet<>();
    Set<Genre> genres = new HashSet<>();
    MpaRating mpaRating;
    Integer mpaRatingId;

//    public Film() {
//    }
//
//    public Film(String name, String description, LocalDate releaseDate, Integer duration) {
//        this.name = name;
//        this.description = description;
//        this.releaseDate = releaseDate;
//        this.duration = duration;
//    }
//
//    public Film(String name, String description, LocalDate releaseDate) {
//        this.name = name;
//        this.description = description;
//        this.releaseDate = releaseDate;
//    }
//
//    public Film(String name, String description, Integer duration) {
//        this.name = name;
//        this.description = description;
//        this.duration = duration;
//    }
//
//    public Film(String name, LocalDate releaseDate, Integer duration) {
//        this.name = name;
//        this.releaseDate = releaseDate;
//        this.duration = duration;
//    }
//
//    public Film(String name, String description) {
//        this.name = name;
//        this.description = description;
//    }
//
//    public Film(String name, LocalDate releaseDate) {
//        this.name = name;
//        this.releaseDate = releaseDate;
//    }
//
//    public Film(String name, int duration) {
//        this.name = name;
//        this.duration = duration;
//    }
//
//    public Film(String name) {
//        this.name = name;
//    }
//
//    public Film(Long id, String name, String description, LocalDate releaseDate, Integer duration) {
//        this.id = id;
//        this.name = name;
//        this.description = description;
//        this.releaseDate = releaseDate;
//        this.duration = duration;
//    }
//
//    public Film(Long id, String name, String description, LocalDate releaseDate) {
//        this.id = id;
//        this.name = name;
//        this.description = description;
//        this.releaseDate = releaseDate;
//    }
//
//    public Film(Long id, String name, String description, Integer duration) {
//        this.id = id;
//        this.name = name;
//        this.description = description;
//        this.duration = duration;
//    }
//
//    public Film(Long id, String name, LocalDate releaseDate, Integer duration) {
//        this.id = id;
//        this.name = name;
//        this.releaseDate = releaseDate;
//        this.duration = duration;
//    }
//
//    public Film(Long id, String name, String description) {
//        this.id = id;
//        this.name = name;
//        this.description = description;
//    }
//
//    public Film(Long id, String name, LocalDate releaseDate) {
//        this.id = id;
//        this.name = name;
//        this.releaseDate = releaseDate;
//    }
//
//    public Film(Long id, String name, Integer duration) {
//        this.id = id;
//        this.name = name;
//        this.duration = duration;
//    }
//
//    public Film(Long id, String name) {
//        this.id = id;
//        this.name = name;
//    }
}
