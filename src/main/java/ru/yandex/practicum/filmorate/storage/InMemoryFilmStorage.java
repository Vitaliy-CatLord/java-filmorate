package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> filmStorage = new HashMap<>();

    @Override
    public Collection<Film> getFilms() {
        return filmStorage.values();
    }

    @Override
    public Film create(Film film) {
        film.setId(getNextId());
        filmStorage.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        filmStorage.put(film.getId(), film);
        return film;
    }

    @Override
    public Film delete(Long id) {
        return filmStorage.remove(id);
    }

    @Override
    public Optional<Film> findById(Long id) {
        return Optional.ofNullable(filmStorage.get(id));
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
