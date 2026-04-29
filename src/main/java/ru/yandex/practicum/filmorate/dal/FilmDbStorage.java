package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class FilmDbStorage extends BaseStorage<Film> {


    private static final String INSERT_QUERY = "INSERT INTO films(" +
            "name, description, releaseDate, duration, mpaRating_id)"
            + "VALUES (?, ?, ?, ?, ?) returning id";
    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String FIND_BY_NAME_QUERY = "SELECT * FROM films WHERE name = ?";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, duration = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM films WHERE id = ?";



    public FilmDbStorage(JdbcTemplate jdbc, FilmRowMapper mapper) {
        super(jdbc, mapper);
    }

    public Film save(Film film) {
        long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                Timestamp.from(Instant.from(film.getReleaseDate())),
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
                film.getDuration()
        );
        return film;
    }

    public boolean delete (long id) {
        return delete(DELETE_QUERY, id);
    }


}
