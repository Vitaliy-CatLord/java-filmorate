package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoudException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;


@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmService {
    FilmStorage filmStorage;
    UserService userService;

    static int MAX_FILM_DESCRIPTION_LENGTH = 200;
    static LocalDate MIN_TIME_OF_RELEASE = LocalDate.of(1895, 12, 28);
    Comparator<Film> filmLikesComparator = Comparator.comparing((Film film) -> film.getLikesUserId().size());

    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Film createFilm(Film newFilm) {
        try {
            validateFilm(newFilm);
            filmStorage.create(newFilm);
            log.info("Добавлен фильм: {}", newFilm);
            return newFilm;
        } catch (ValidationException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public Film updateFilm(Film newFilm) {
        try {
            validateFilm(newFilm);
            return setOldFilm(newFilm);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }


    public Collection<Film> getAllFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(Long id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new NotFoudException("Фильм с id " + id + " не найден"));

    }

    public void addLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        User user = userService.getUserById(userId);

        film.getLikesUserId().add(userId);
        log.info("Пользователь {} поставил лайк фильму {}.", user.getName(), film.getName());
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        User user = userService.getUserById(userId);

        film.getLikesUserId().remove(userId);
        log.info("Пользователь {} удалил лайк у фильма {}.", user.getName(), film.getName());
    }

    public List<Film> getTopFilms(int countOfTop) {
        if (countOfTop < 0) {
            throw new ValidationException("Число наиболее популярных фильмов не может быть отрицательным");
        }
        log.info("Получение топ {} по количеству лайков", countOfTop);
        return filmStorage.getFilms().stream()
                .sorted(filmLikesComparator.reversed())
                .limit(countOfTop)
                .toList();
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
            String message = "Идентификатор должен быть указан";
            log.warn(message);
            throw new ValidationException(message);
        }
        //верификация фильма только по id, остальные поля могут все же совпадать
        if (filmStorage.findById(newFilm.getId()).isPresent()) {
            Film oldFilm = filmStorage.findById(newFilm.getId()).get();

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
            throw new NotFoudException("Фильма с ID " + newFilm.getId() + " не существует");
        }
    }


    public void cleanStorage() {
        filmStorage.cleanStorage();
    }
}
