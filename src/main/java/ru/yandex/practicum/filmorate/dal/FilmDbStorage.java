package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.*;
import java.util.stream.Collectors;

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

    private static final String GET_COMMON_FILMS_QUERY = """
            SELECT f.*
            FROM films AS f
            JOIN likes AS l1 ON f.id = l1.film_id AND l1.user_id = ?
            JOIN likes AS l2 ON f.id = l2.film_id AND l2.user_id = ?
            LEFT JOIN likes AS l_all ON f.id = l_all.film_id
            GROUP BY f.id, f.name, f.description, f.releaseDate, f.duration, f.mpaRating_id
            ORDER BY COUNT(l_all.user_id) DESC;
            """;

    private static final String GET_DIRECTOR_FILMS_BY_LIKES_QUERY = """
            SELECT films.*
            FROM films
            JOIN film_directors fd ON films.id = fd.film_id
            LEFT JOIN likes ON films.id = likes.film_id
            WHERE fd.director_id = ?
            GROUP BY films.id
            ORDER BY COUNT(likes.user_id) DESC
            """;

    private static final String GET_DIRECTOR_FILMS_BY_YEAR_QUERY = """
            SELECT films.*
            FROM films
            JOIN film_directors fd ON films.id = fd.film_id
            WHERE fd.director_id = ?
            ORDER BY films.releaseDate ASC
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
            """;
    private static final String DELETE_FILM_LIKES_QUERY = "DELETE FROM likes WHERE film_id = ?";

    private static final String GET_RECOMMENDATIONS_QUERY = """
            WITH shared_likes AS (
                SELECT user_id, COUNT(film_id) as count_likes
                FROM likes
                WHERE film_id IN (SELECT film_id FROM likes WHERE user_id = ?) AND user_id <> ?
                GROUP BY user_id
            )
            SELECT DISTINCT f.*
            FROM films f
            JOIN likes l ON f.id = l.film_id
            WHERE l.user_id IN (
                SELECT user_id
                FROM shared_likes
                WHERE count_likes = (SELECT MAX(count_likes) FROM shared_likes)
            )
            AND f.id NOT IN (SELECT film_id FROM likes WHERE user_id = ?)
            """;

    private static final String ADD_DIRECTOR_QUERY =
            "INSERT INTO film_directors(film_id, director_id) VALUES (?, ?)";
    private static final String GET_FILM_DIRECTORS_QUERY = """
            SELECT d.id AS director_id, d.name
            FROM directors d
            JOIN film_directors fd ON d.id = fd.director_id
            WHERE fd.film_id = ?
            """;
    private static final String DELETE_FILM_DIRECTORS_QUERY =
            "DELETE FROM film_directors WHERE film_id = ?";

    private static final String SEARCH_BY_TITLE_QUERY = """
            SELECT f.*
            FROM films f
            LEFT JOIN likes l ON f.id = l.film_id
            WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', ?, '%'))
            GROUP BY f.id
            ORDER BY COUNT(l.user_id) DESC
            """;

    private static final String SEARCH_BY_DIRECTOR_QUERY = """
            SELECT f.*
            FROM films f
            JOIN film_directors fd ON f.id = fd.film_id
            JOIN directors d ON fd.director_id = d.id
            LEFT JOIN likes l ON f.id = l.film_id
            WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', ?, '%'))
            GROUP BY f.id
            ORDER BY COUNT(l.user_id) DESC
            """;

    private static final String SEARCH_BY_TITLE_AND_DIRECTOR_QUERY = """
            SELECT f.*
            FROM films f
            LEFT JOIN film_directors fd ON f.id = fd.film_id
            LEFT JOIN directors d ON fd.director_id = d.id
            LEFT JOIN likes l ON f.id = l.film_id
            WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', ?, '%'))
               OR LOWER(d.name) LIKE LOWER(CONCAT('%', ?, '%'))
            GROUP BY f.id
            ORDER BY COUNT(l.user_id) DESC
            """;

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
        updateDirectors(film);
        loadLGR(film);
        return film;
    }

    public List<Film> findAll() {
        List<Film> films = findMany(FIND_ALL_QUERY);
        films.forEach(this::loadLGR);
        return films;
    }

    public Optional<Film> findById(long id) {
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
        updateDirectors(film);
        loadLGR(film);
        return film;
    }

    public boolean delete(long filmId) {
        jdbc.update(DELETE_FILM_DIRECTORS_QUERY, filmId);
        jdbc.update(DELETE_FILM_GENRES_QUERY, filmId);
        jdbc.update(DELETE_FILM_LIKES_QUERY, filmId);
        return delete(DELETE_QUERY, filmId);
    }

    public List<Film> getTopFilms(Integer count, Integer genreId, Integer year) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT f.* FROM films AS f ");
        sql.append("LEFT JOIN likes AS l ON f.id = l.film_id ");

        if (genreId != null) {
            sql.append("LEFT JOIN film_genres AS fg ON f.id = fg.film_id ");
        }

        sql.append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (genreId != null) {
            sql.append("AND fg.genre_id = ? ");
            params.add(genreId);
        }

        if (year != null) {
            sql.append("AND EXTRACT(YEAR FROM f.releaseDate) = ? ");
            params.add(year);
        }

        sql.append("GROUP BY f.id ");
        sql.append("ORDER BY COUNT(DISTINCT l.user_id) DESC, f.id ASC ");
        sql.append("LIMIT ?");
        params.add(count);

        List<Film> films = findMany(sql.toString(), params.toArray());
        films.forEach(this::loadLGR);
        return films;
    }

    public List<Film> getCommonFilms(long userId, long friendId) {
        List<Film> films = findMany(GET_COMMON_FILMS_QUERY, userId, friendId);
        films.forEach(this::loadLGR);
        return films;
    }

    public List<Film> getFilmsByDirectorSortedByLikes(long directorId) {
        List<Film> films = findMany(GET_DIRECTOR_FILMS_BY_LIKES_QUERY, directorId);
        films.forEach(this::loadLGR);
        return films;
    }

    public List<Film> getFilmsByDirectorSortedByYear(long directorId) {
        List<Film> films = findMany(GET_DIRECTOR_FILMS_BY_YEAR_QUERY, directorId);
        films.forEach(this::loadLGR);
        return films;
    }

    public void addLike(long userId, long filmId) {
        jdbc.update(ADD_LIKE_QUERY, userId, filmId);
    }

    public List<Long> getFilmLikes(long filmId) {
        return jdbc.queryForList(GET_FILM_LIKES_QUERY, Long.class, filmId);
    }

    public void removeLike(long userId, long filmId) {
        jdbc.update(REMOVE_LIKE_QUERY, userId, filmId);
    }

    public void updateGenres(Film film) {
        //Удаляет все существующие связи
        jdbc.update(DELETE_FILM_GENRES_QUERY, film.getId());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            List<Genre> uniqueGenres = film.getGenres().stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(
                            Genre::getGenreId,
                            genre -> genre,
                            (existing, replacement) -> existing
                    ))
                    .values()
                    .stream()
                    .toList();
            for (Genre g : uniqueGenres) {
                if (g.getGenreId() > 0) {
                    jdbc.update(ADD_GENRE_QUERY, film.getId(), g.getGenreId());

                } else {
                    // Логирование или исключение для отладки
                    log.warn("Жанр задан неверно: {}", g);
                }
            }
        }
    }

    public void updateDirectors(Film film) {
        jdbc.update(DELETE_FILM_DIRECTORS_QUERY, film.getId());

        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            List<Director> uniqueDirectors = film.getDirectors().stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(
                            Director::getId,
                            d -> d,
                            (existing, replacement) -> existing
                    ))
                    .values()
                    .stream()
                    .toList();
            for (Director d : uniqueDirectors) {
                if (d.getId() > 0) {
                    jdbc.update(ADD_DIRECTOR_QUERY, film.getId(), d.getId());
                } else {
                    log.warn("Режиссёр задан неверно: {}", d);
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
            System.out.println("Ошибка загрузки рейтинга: " + e.getMessage());
        }

        List<Director> directors = jdbc.query(GET_FILM_DIRECTORS_QUERY,
                (rs, i) -> {
                    Director director = new Director();
                    director.setId(rs.getLong("director_id"));
                    director.setName(rs.getString("name"));
                    return director;
                },
                film.getId());
        film.setDirectors(new ArrayList<>(directors));
    }

    public List<Film> searchFilms(String query, boolean byTitle, boolean byDirector) {
        List<Film> films;
        if (byTitle && byDirector) {
            films = findMany(SEARCH_BY_TITLE_AND_DIRECTOR_QUERY, query, query);
        } else if (byTitle) {
            films = findMany(SEARCH_BY_TITLE_QUERY, query);
        } else {
            films = findMany(SEARCH_BY_DIRECTOR_QUERY, query);
        }
        films.forEach(this::loadLGR);
        return films;
    }

    public List<Film> getRecommendations(long userId) {
        List<Film> films = findMany(GET_RECOMMENDATIONS_QUERY, userId, userId, userId);
        films.forEach(this::loadLGR);
        return films;
    }
}
