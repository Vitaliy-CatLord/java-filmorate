package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RestControllerAdvice
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    private static final String SETTING_LIKES = "/{id}/like/{userId}";

    @PostMapping
    public FilmDto createFilm(@Valid @RequestBody NewFilmRequest newFilm) {
        log.info("Выполнение запроса на добавление фильма {}", newFilm);
        return filmService.createFilm(newFilm);
    }

    @GetMapping
    public Collection<FilmDto> getAllFilms() {
        log.info("Выполнение запроса на получение всех фильмов");
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public FilmDto getFilmById(@PathVariable Long id) {
        log.info("Выполнение запроса на получение фильма с ID {}", id);
        return filmService.getFilmById(id);
    }

    @PutMapping
    public FilmDto updateFilm(@Valid @RequestBody UpdateFilmRequest updatedFilm) {
        log.info("Выполнение запроса на изменение фильма с ID {}", updatedFilm.getId());
        return filmService.updateFilm(updatedFilm);
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
    public List<FilmDto> getTopFilms(@RequestParam(value = "count", defaultValue = "10") Integer count) {
        log.info("Выполнение запроса на получение ТОП{} популярных фильмов", count);
        return filmService.getTopFilms(count);
    }

}
