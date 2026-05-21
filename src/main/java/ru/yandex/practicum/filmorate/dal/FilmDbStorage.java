package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
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

    //если будет падать, добавить группировку по режиссеру
    private static final String GET_COMMON_FILMS_QUERY = """
            SELECT f.*
            FROM films AS f
            JOIN likes AS l1 ON f.id = l1.film_id AND l1.user_id = ?
            JOIN likes AS l2 ON f.id = l2.film_id AND l2.user_id = ?
            LEFT JOIN likes AS l_all ON f.id = l_all.film_id
            GROUP BY f.id, f.name, f.description, f.releaseDate, f.duration, f.mpaRating_id
            ORDER BY COUNT(l_all.user_id) DESC;
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
        loadLGR(film);
        return film;
    }

    public boolean delete(long filmId) {
        jdbc.update(DELETE_FILM_GENRES_QUERY, filmId);
        jdbc.update(DELETE_FILM_LIKES_QUERY, filmId);
        return delete(DELETE_QUERY, filmId);
    }

    /**
     * Возвращает список популярных фильмов из базы данных с возможностью фильтрации.
     * Строит динамический SQL-запрос в зависимости от переданных фильтров по жанру и году.
     *
     * @param count   ограничение количества записей (LIMIT)
     * @param genreId ID жанра для фильтрации (если null, фильтр не применяется)
     * @param year    год релиза для фильтрации (если null, фильтр не применяется)
     * @return список объектов Film с заполненными связями
     */
    public List<Film> getTopFilms(int count, Integer genreId, Integer year) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT f.* FROM films AS f ");
        sql.append("LEFT JOIN likes AS l ON f.id = l.film_id ");

        // Если задан жанр, добавляем таблицу film_genres
        if (genreId != null) {
            sql.append("LEFT JOIN film_genres AS fg ON f.id = fg.film_id ");
        }

        sql.append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        // Добавляем условие фильтрации по жанру
        if (genreId != null) {
            sql.append("AND fg.genre_id = ? ");
            params.add(genreId);
        }

        // Добавляем условие фильтрации по году
        if (year != null) {
            sql.append("AND EXTRACT(YEAR FROM f.releaseDate) = ? ");
            params.add(year);
        }

        sql.append("GROUP BY f.id ");
        sql.append("ORDER BY COUNT(DISTINCT l.user_id) DESC ");
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
    }
}
