package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Comparator;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmService {
    UserDbStorage usersStorage;
    FilmDbStorage filmStorage;
    RatingDbStorage ratingStorage;
    GenreDbStorage genreStorage;
    FriendshipStatusDbStorage friendshipStatusStorage;

    Comparator<Film> filmLikesComparator = Comparator.comparing((Film film) -> film.getLikesUserId().size());


    public FilmDto createFilm(NewFilmRequest request) {

        if (request.getMpaRating() != null) {
            ratingStorage.findById(request.getMpaRating().getMpaRatingId())
                    .orElseThrow(() -> new NotFoundException("Рейтинг не найден"));
            ;
        }

        if (request.getGenres() != null) {
            for (Genre genre : request.getGenres()) {
                genreStorage.findById(genre.getGenreId())
                        .orElseThrow(() -> new NotFoundException("Жанр не найден"));
                ;
            }
        }

        Film film = FilmMapper.mapToFilm(request);
        log.info("Добавлен фильм: {}", film);
        return FilmMapper.mapToFilmDto(filmStorage.save(film));
    }

    public FilmDto updateFilm(UpdateFilmRequest request) {
        Film film = filmStorage.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Фильм с id " + request.getId() + " не найден"));

        if (request.getGenres() != null) {
            for (Genre genre : request.getGenres()) {
                genreStorage.findById(genre.getGenreId());
            }
        }

        if (request.getMpaRating() != null) {
            ratingStorage.findById(request.getMpaRating().getMpaRatingId());
        }

        FilmMapper.updateFilmFields(film, request);
        log.info("Обновлен фильм: {}", film);
        return FilmMapper.mapToFilmDto(filmStorage.update(film));
    }


    public List<FilmDto> getAllFilms() {
        return filmStorage.findAll().stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    public FilmDto getFilmById(Long id) {
        return filmStorage.findById(id)
                .map(FilmMapper::mapToFilmDto)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
    }

    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден"));
        User user = usersStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        filmStorage.addLike(userId, filmId);
        log.info("Пользователь {} поставил лайк фильму {}.", user.getName(), film.getName());
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден"));
        User user = usersStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        filmStorage.removeLike(userId, filmId);
        log.info("Пользователь {} удалил лайк у фильма {}.", user.getName(), film.getName());
    }

    public List<FilmDto> getTopFilms(int countOfTop) {
        if (countOfTop < 0) {
            throw new ValidationException("Число наиболее популярных фильмов не может быть отрицательным");
        }
        log.info("Получение топ {} по количеству лайков", countOfTop);
        return filmStorage.getTopFilms(countOfTop)
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }


}
