package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class GenreDbStorage extends BaseStorage<Genre> {
    public GenreDbStorage(JdbcTemplate jdbc, GenreRowMapper mapper) {
        super(jdbc, mapper);
    }

    private static final String INSERT_QUERY = "INSERT INTO genre (name) VALUES (?) returning genre_id";
    private static final String FIND_ALL_QUERY = "SELECT * FROM genre";
    private static final String FIND_BY_NAME_QUERY = "SELECT * FROM genre WHERE name = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genre WHERE id = ?";
    private static final String UPDATE_QUERY = "UPDATE genre SET name = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM genre WHERE id = ?";

    public Genre save(Genre genre) {
        Integer id = (int) insert(INSERT_QUERY, genre.getName());
        genre.setGenreId(id);
        return genre;
    }

    public List<Genre> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Genre> findById (long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public List<Genre> findByName(String name) {
        return findMany(FIND_BY_NAME_QUERY, name);
    }

    public Genre update(Genre genre) {
        update(
                UPDATE_QUERY,
                genre.getName()
        );
        return genre;
    }

    public boolean delete (long id) {
        return delete(DELETE_QUERY, id);
    }
}
