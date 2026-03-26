package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RestControllerAdvice
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    private static final String SETTING_LIKES = "/{id}/like/{userId}";

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Выполнение запроса на получение всех фильмов");
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        log.info("Выполнение запроса на получение фильма с ID {}", id);
        return filmService.getFilmById(id);
    }

    @PostMapping
    public Film createFilm(@RequestBody Film newFilm) {
        log.info("Выполнение запроса на добавление фильма {}", newFilm.getName());
        return filmService.createFilm(newFilm);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {
        log.info("Выполнение запроса на изменение фильма {}", newFilm.getName());
        return filmService.updateFilm(newFilm);
    }

    @PutMapping(SETTING_LIKES)
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Выполнение запроса на добавление фильму с ID {} лайка от  пользователя с ID {}", id, userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping(SETTING_LIKES)
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Выполнение запроса на удаление у фильма с ID {} лайка от  пользователя с ID {}", id, userId);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam(value = "count", defaultValue = "10") Integer count) {
        log.info("Выполнение запроса на получение ТОП{} популярных фильмов", count);
        return filmService.getTopFilms(count);
    }

}
