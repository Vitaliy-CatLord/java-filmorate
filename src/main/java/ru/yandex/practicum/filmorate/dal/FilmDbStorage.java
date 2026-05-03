package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class FilmDbStorage extends BaseStorage<Film> {


    private static final String INSERT_QUERY =
            "INSERT INTO films(name, description, releaseDate, duration, mpaRating_id) "
            + "VALUES (?, ?, ?, ?, ?)";
    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String FIND_BY_NAME_QUERY = "SELECT * FROM films WHERE name = ?";
    private static final String UPDATE_QUERY = "UPDATE films " +
            "SET name = ?, description = ?, releaseDate = ?, duration = ?, mpaRating_id = ? WHERE id = ?";
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
            SELECT *
            FROM mpaRating
            WHERE mpaRating_id = ?
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
        updateGenres(film);
        loadLGR(film);
        return film;
    }

    public List<Film> findAll() {
        List<Film> films = findMany(FIND_ALL_QUERY);
        films.forEach(this::loadLGR);
        return films;
    }

    public Optional<Film> findById (long id) {
        Optional<Film> film = findOne(FIND_BY_ID_QUERY, id);
        film.ifPresent(this::loadLGR);
        return film;
    }

    public List<Film> findByName(String name) {
        List<Film> films = findMany(FIND_BY_NAME_QUERY, name);
        films.forEach(this::loadLGR);
        return films;
    }

    public Film update(Film film) {
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpaRatingId(),
                film.getId()
        );
        updateGenres(film);
        loadLGR(film);
        return film;
    }

    public boolean delete (long id) {
        return delete(DELETE_QUERY, id);
    }

    public List<Film> getTopFilms(int count) {
        List<Film> films = findMany(GET_TOP_FILMS_QUERY, count);
        films.forEach(this::loadLGR);
        return films;
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

//    public List<Genre> getFilmGenres(long filmId) {
//        return jdbc.query(GET_FILM_GENRES_QUERY, (rs, rowNum) -> {
//            Genre genre = new Genre();
//            genre.setGenreId(rs.getInt("genre_id"));
//            genre.setName(rs.getString("name"));
//            return genre;
//        }, filmId);
//    }
//
//    public MpaRating getFilmRating(long filmId) {
//        return jdbc.queryForObject(GET_FILM_RATING_QUERY, (rs, rowNum) -> {
//            MpaRating rating = new MpaRating();
//            rating.setName(rs.getString("name"));
//            return rating;
//        }, filmId);
//    }


    public void updateGenres(Film film) {
        //Удаляет все существующие связи
        jdbc.update(DELETE_FILM_GENRES_QUERY, film.getId());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre g : film.getGenres()) {
                // Проверяем, что объект жанра не null и у него есть id
                if (g != null && g.getGenreId() >0 ) {
                        jdbc.update(ADD_GENRE_QUERY, film.getId(), g.getGenreId());

                } else {
                // Логирование или исключение для отладки
                log.warn("Genre with null or invalid ID found: {}", g);
            }
            }
        }
    }

    public void loadLGR(Film film) {
        //Likes
        List<Long> likes = jdbc.queryForList(GET_FILM_LIKES_QUERY, Long.class, film.getId());
        film.setLikesUserId(new HashSet<>(likes));

        //Genres
        List<Genre> genres = jdbc.query(GET_FILM_GENRES_QUERY,
                (rs, i) -> {
                    Genre genre = new Genre();
                    genre.setGenreId(rs.getInt("genre_id"));
                    genre.setName(rs.getString("name"));
                    return genre;
                },
                film.getId());
        film.setGenres(new ArrayList<>(genres));

        //Rating
        try {
            MpaRating mpaRating = jdbc.queryForObject(GET_FILM_RATING_QUERY,
                    (rs, rowNum) -> {
                        MpaRating rating = new MpaRating();
                        rating.setMpaRatingId(rs.getInt("mpaRating_id"));
                        rating.setName(rs.getString("name"));
                        return rating;
                    },
                    film.getMpaRatingId());
            film.setMpaRating(mpaRating);
        } catch (Exception e) {
            System.out.println("Error loading MPA rating: " + e.getMessage());
        }
    }
}
