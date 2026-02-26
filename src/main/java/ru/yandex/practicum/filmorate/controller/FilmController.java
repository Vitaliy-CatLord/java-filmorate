package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class FilmController {

    private final Map<Long, Film> filmsBase = new HashMap<>();

    private static final int MAX_FILM_DESCRIPTION_LENGTH = 200;
    private static final Instant MIN_TIME_OF_RELEASE= Instant.from(LocalDateTime.of(1895, 12, 28, 12, 0));

    @PostMapping
    public Film addFilm (@RequestBody Film newFilm){
        if(newFilm.getName() == null || newFilm.getName().isBlank()) {
            throw new ValidationException("Незаполненно поле Название фильма");
        }
        if(newFilm.getDescription().length() > MAX_FILM_DESCRIPTION_LENGTH) {
            throw new ValidationException("Максимальная длина описания — " + MAX_FILM_DESCRIPTION_LENGTH + "символов");
        }
        if(newFilm.getReleaseDate().isBefore(MIN_TIME_OF_RELEASE)) {
            throw new ValidationException("Дата релиза указана неверно");
        }
        if(newFilm.getDuration().toSeconds() < 0) {
            throw new ValidationException("Длительность фильма не может быть отрицательной");
        }

        newFilm.setId(getNextId());
        filmsBase.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {
        if(newFilm.getName() == null || newFilm.getName().isBlank()) {
            throw new ValidationException("Незаполненно поле Название фильма");
        }
        if(newFilm.getDescription().length() > MAX_FILM_DESCRIPTION_LENGTH) {
            throw new ValidationException("Максимальная длина описания — " + MAX_FILM_DESCRIPTION_LENGTH + "символов");
        }
        if(newFilm.getReleaseDate().isBefore(MIN_TIME_OF_RELEASE)) {
            throw new ValidationException("Дата релиза указана неверно");
        }
        if(newFilm.getDuration().toSeconds() < 0) {
            throw new ValidationException("Длительность фильма не может быть отрицательной");
        }

        if(filmsBase.containsKey(newFilm.getId())) {
            Film oldFilm = filmsBase.get(newFilm.getId());
            oldFilm = newFilm;
            return oldFilm;
        }
        throw new ValidationException("Фильма с таким ID не существует");
    }


    private long getNextId() {
        long currentMaxId =  filmsBase.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
