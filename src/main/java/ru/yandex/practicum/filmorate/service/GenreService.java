package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.GenreDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoudException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GenreService {
    GenreDbStorage genreDbStorage;

    public List<Genre> getGenres() {
        return genreDbStorage.findAll();
    }

    public Genre getGenreById(long id) {
        return genreDbStorage.findById(id)
                .orElseThrow(() -> new NotFoudException("Жанр не найден"));
    }

}
