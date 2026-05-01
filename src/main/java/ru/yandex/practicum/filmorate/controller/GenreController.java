package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@Slf4j
@RestController
@RestControllerAdvice
@RequestMapping("/genre")
@RequiredArgsConstructor
public class GenreController {
    GenreService genreService;

    @GetMapping
    public Collection<Genre> getAll() {
        log.info("Выполнение запроса на получение всех жанров");
        return genreService.getGenres();
    }

    @GetMapping("/{id}")
    public Genre getFilmById(@PathVariable int id) {
        log.info("Выполнение запроса на получение жанра с ID {}", id);
        return genreService.getGenreById(id);
    }
}
