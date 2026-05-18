package ru.yandex.practicum.filmorate.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Comparator;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilmMapper {

    public static Film mapToFilm(NewFilmRequest request) {
        Film film = new Film();

        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());

        if (request.getMpaRating() != null) {
            film.setMpaRating(request.getMpaRating());
            film.setMpaRatingId(request.getMpaRating().getMpaRatingId());
        }
        if (request.getGenres() != null) {
            film.setGenres(request.getGenres());
        }
        return film;
    }

    public static FilmDto mapToFilmDto(Film film) {
        FilmDto dto = new FilmDto();

        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());
        dto.setMpa(film.getMpaRating());
        dto.setGenres(film.getGenres()
                .stream()
                .sorted((Comparator.comparing(Genre::getGenreId)))
                .collect(Collectors.toList()));

        return dto;
    }

    public static Film updateFilmFields(Film film, UpdateFilmRequest request) {
        if (request.hasName()) {
            film.setName(request.getName());
        }
        if (request.hasDescription()) {
            film.setDescription(request.getDescription());
        }
        if (request.hasReleaseDate()) {
            film.setReleaseDate(request.getReleaseDate());
        }
        if (request.hasDuration()) {
            film.setDuration(request.getDuration());
        }
        if (request.hasMpa()) {
            film.setMpaRating(request.getMpaRating());
            film.setMpaRatingId(request.getMpaRating().getMpaRatingId());
        }
        if (request.hasGenres()) {
            film.setGenres(request.getGenres());
        }

        return film;
    }
}