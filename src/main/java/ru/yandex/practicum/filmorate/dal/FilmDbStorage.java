package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository
public class FilmDbStorage extends BaseStorage<Film> {


    private static final String INSERT_QUERY =
            "INSERT INTO films(name, description, releaseDate, duration, mpaRating_id)"
            + "VALUES (?, ?, ?, ?, ?) returning id";
    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String FIND_BY_NAME_QUERY = "SELECT * FROM films WHERE name = ?";
    private static final String UPDATE_QUERY = "UPDATE films " +
            "SET name = ?, description = ?, release_date = ?, duration = ?, mpaRating_id = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM films WHERE id = ?";

    private static final String GET_TOP_FILMS_QUERY = """
            SELECT films.*
            FROM films
            LEFT JOIN likes ON films.id = likes.film_id
            GROUP BY films.id
            ORDER BY COUNT(likes.user_id) DESC
            LIMIT ?
            """;

    private static final String ADD_LIKE_QUERY = "INSERT INTO likes (user_id, film_id) VALUES (?, ?)";
    private static final String GET_FILM_LIKES_QUERY = "SELECT user_id FROM likes WHERE film_id = ?";
    private static final String REMOVE_LIKE_QUERY = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";

    private static final String ADD_GENRE_QUERY = "INSERT INTO film_genres(film_id, genre_id) VALUES (?, ?)";
    private static final String GET_FILM_GENRES_QUERY = """
            SELECT g.genre_id, g.name
            FROM genres AS g
            JOIN film_genres AS fg ON g.genre_id = fg.genre_id
            WHERE fg.film_id = ?
            """;
    private static final String DELETE_FILM_GENRES_QUERY = "DELETE FROM film_genres WHERE film_id = ?";

    private static final String GET_FILM_RATING_QUERY = """
            SELECT m.name
            FROM mpa AS m
            JOIN films AS f ON m.mpaRating_id = f.mpaRating_id
            WHERE f.id = ?
            """ ;

    public FilmDbStorage(JdbcTemplate jdbc, FilmRowMapper mapper) {
        super(jdbc, mapper);
    }

    public Film save(Film film) {
        long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpaRatingId());
        film.setId(id);
        return film;
    }

    public List<Film> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Film> findById (long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public List<Film> findByName(String name) {
        return findMany(FIND_BY_NAME_QUERY, name);
    }

    public Film update(Film film) {
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpaRatingId()
        );
        return film;
    }

    public boolean delete (long id) {
        return delete(DELETE_QUERY, id);
    }

    public List<Film> getTopFilms(int count) {
        return findMany(GET_TOP_FILMS_QUERY, count);
    }

    public void addLike(long userId, long filmId) {
        jdbc.update(ADD_LIKE_QUERY, userId, filmId);
    }

    public List<Long> getFilmLikes (long filmId) {
        return jdbc.queryForList(GET_FILM_LIKES_QUERY, Long.class, filmId);
    }

    public void removeLike (long userId, long filmId) {
        jdbc.update(REMOVE_LIKE_QUERY, userId, filmId);
    }

    public void updateGenres (Film film) {
        //сначала удаляем все жанры, а потом добавляем имеющиеся у переданного фильма
        jdbc.update(DELETE_FILM_GENRES_QUERY, film.getId());
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbc.update(ADD_GENRE_QUERY, film.getId(), genre.getGenreId());
            }
        }
    }

    public List<String> getFilmGenres (long filmId) {
        return jdbc.queryForList(GET_FILM_GENRES_QUERY, String.class, filmId);
    }

    public List<String> getFilmRating (long filmId) {
        return jdbc.queryForList(GET_FILM_GENRES_QUERY, String.class, filmId);
    }


}
