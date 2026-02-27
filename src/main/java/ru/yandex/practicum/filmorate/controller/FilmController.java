package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> filmStorage = new HashMap<>();
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);

    private static final int MAX_FILM_DESCRIPTION_LENGTH = 200;
    private static final Instant MIN_TIME_OF_RELEASE = LocalDateTime.of(1895, 12, 28, 12, 0)
            .atZone(ZoneId.of("Europe/Paris"))
            .toInstant();

    @PostMapping
    public Film postFilm(@RequestBody Film newFilm) {
        try {
            if (newFilm.getName() == null || newFilm.getName().isBlank()) {
                throw new ValidationException("Незаполненно поле Название фильма");
            }
            if (newFilm.getDescription().length() > MAX_FILM_DESCRIPTION_LENGTH) {
                throw new ValidationException("Максимальная длина описания — " + MAX_FILM_DESCRIPTION_LENGTH + "символов");
            }
            if (newFilm.getReleaseDate().isBefore(MIN_TIME_OF_RELEASE)) {
                throw new ValidationException("Дата релиза указана неверно");
            }
            if (newFilm.getDuration().toSeconds() < 0) {
                throw new ValidationException("Длительность фильма не может быть отрицательной");
            }

            newFilm.setId(getNextId());
            filmStorage.put(newFilm.getId(), newFilm);
            return newFilm;
        } catch (Exception e) {
            log.error(e.getMessage());
            return newFilm;
        }

    }

    @PutMapping
    public Film putFilm(@RequestBody Film newFilm) {
        try {
            // верификация фильма только по id, все остальное изменяемо
            if (filmStorage.containsKey(newFilm.getId())) {
                Film oldFilm = filmStorage.get(newFilm.getId());

                if (newFilm.getName() != null && !newFilm.getName().isBlank()) {
                    oldFilm.setName(newFilm.getName());
                }
                if (newFilm.getDescription() != null && !newFilm.getDescription().isBlank()) {
                    if (newFilm.getDescription().length() > MAX_FILM_DESCRIPTION_LENGTH) {
                        throw new ValidationException("Максимальная длина описания — " + MAX_FILM_DESCRIPTION_LENGTH + "символов");
                    }
                    oldFilm.setDescription(newFilm.getDescription());
                }
                if (newFilm.getReleaseDate() != null) {
                    if (newFilm.getReleaseDate().isBefore(MIN_TIME_OF_RELEASE)) {
                        throw new ValidationException("Дата релиза указана неверно");
                    }
                    oldFilm.setReleaseDate(newFilm.getReleaseDate());
                }
                if (newFilm.getDuration() != null) {
                    if (newFilm.getDuration().toSeconds() < 0) {
                        throw new ValidationException("Длительность фильма не может быть отрицательной");
                    }
                    oldFilm.setDuration(newFilm.getDuration());
                }
                return oldFilm;
            } else {
                throw new ValidationException("Фильма с таким ID не существует");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return newFilm;
        }


    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmStorage.values();
    }


    private long getNextId() {
        long currentMaxId = filmStorage.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public void cleanStorage() {
        filmStorage.clear();
    }
}
