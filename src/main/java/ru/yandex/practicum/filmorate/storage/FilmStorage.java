package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> getFilms();

    Film create(Film film);

    Film update(Film film);

    Film delete(Long id);

    Optional<Film> findById(Long id);

    public void cleanStorage();
}
