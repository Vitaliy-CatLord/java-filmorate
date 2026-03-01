package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> filmStorage = new HashMap<>();
    private final Logger log = LoggerFactory.getLogger(FilmController.class);

    private static final int MAX_FILM_DESCRIPTION_LENGTH = 200;
    private static final LocalDate MIN_TIME_OF_RELEASE = LocalDate.of(1895, 12, 28);

    @PostMapping
    public Film postFilm(@RequestBody Film newFilm) {
        try {
            validateFilm(newFilm);
            newFilm.setId(getNextId());
            filmStorage.put(newFilm.getId(), newFilm);
            log.info("Добавлен фильм: {}", newFilm);
            return newFilm;
        } catch (ValidationException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @PutMapping
    public Film putFilm(@RequestBody Film newFilm) {
        try {
            validateFilm(newFilm);
            return setOldFilm(newFilm);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmStorage.values();
    }

    private void validateFilm(Film newFilm) throws ValidationException {
        if (newFilm.getName() == null || newFilm.getName().isBlank()) {
            throw new ValidationException("Незаполненно поле Название фильма");
        }
        if (newFilm.getDescription() != null && newFilm.getDescription().length() > MAX_FILM_DESCRIPTION_LENGTH) {
            throw new ValidationException(String.format("Максимальная длина описания — %s символов",
                    MAX_FILM_DESCRIPTION_LENGTH));
        }
        if (newFilm.getReleaseDate() != null && (newFilm.getReleaseDate().isBefore(MIN_TIME_OF_RELEASE)
                || newFilm.getReleaseDate().isAfter(LocalDate.now()))) {
            throw new ValidationException("Дата релиза указана неверно");
        }
        if (newFilm.getDuration() != null && newFilm.getDuration() < 0) {
            throw new ValidationException("Длительность фильма должна быть положительным числом.");
        }
    }

    private Film setOldFilm(Film newFilm) throws ValidationException {
        if (newFilm.getId() == null) {
            String message = "Id должен быть указан";
            log.warn(message);
            throw new ValidationException(message);
        }
        //верификация фильма только по id, остальные поля могут все же совпадать
        if (filmStorage.containsKey(newFilm.getId())) {
            Film oldFilm = filmStorage.get(newFilm.getId());

            if (newFilm.getName() != null && !newFilm.getName().isBlank()) {
                oldFilm.setName(newFilm.getName());
            }
            if (newFilm.getDescription() != null && !newFilm.getDescription().isBlank()) {
                if (newFilm.getDescription().length() > MAX_FILM_DESCRIPTION_LENGTH) {
                    throw new ValidationException(String.format("Максимальная длина описания — %s символов",
                            MAX_FILM_DESCRIPTION_LENGTH));
                }
                oldFilm.setDescription(newFilm.getDescription());
            }
            if (newFilm.getReleaseDate() != null) {
                if (newFilm.getReleaseDate().isBefore(MIN_TIME_OF_RELEASE)
                        || newFilm.getReleaseDate().isAfter(LocalDate.now())) {
                    throw new ValidationException("Дата релиза указана неверно");
                }
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
            }
            if (newFilm.getDuration() != null) {
                if (newFilm.getDuration() < 0) {
                    throw new ValidationException("Длительность фильма не может быть отрицательной");
                }
                oldFilm.setDuration(newFilm.getDuration());
            }
            log.info("Изменен фильм, новые данные: {}", oldFilm);
            return oldFilm;
        } else {
            throw new ValidationException("Фильма с таким ID не существует");
        }
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
