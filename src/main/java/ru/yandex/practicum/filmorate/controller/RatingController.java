package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.Collection;

@Slf4j
@RestController
@RestControllerAdvice
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    @GetMapping
    public Collection<MpaRating> getAll() {
        log.info("Выполнение запроса на получение списка рейтингов");
        return ratingService.getRating();
    }

    @GetMapping("/{id}")
    public MpaRating getFilmById(@PathVariable long id) {
        log.info("Выполнение запроса на получение рейтинга с ID {}", id);
        return ratingService.getRatingById(id);
    }
}
