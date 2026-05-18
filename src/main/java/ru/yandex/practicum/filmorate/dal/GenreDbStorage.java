package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository
public class GenreDbStorage extends BaseStorage<Genre> {
    public GenreDbStorage(JdbcTemplate jdbc, GenreRowMapper mapper) {
        super(jdbc, mapper);
    }

    private static final String INSERT_QUERY = "INSERT INTO genres (name) VALUES (?)";
    private static final String FIND_ALL_QUERY = "SELECT * FROM genres";
    private static final String FIND_BY_NAME_QUERY = "SELECT * FROM genres WHERE name = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE genre_id = ?";
    private static final String UPDATE_QUERY = "UPDATE genres SET name = ? WHERE genre_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM genres WHERE genre_id = ?";

    public Genre save(Genre genre) {
        int id = (int) insert(INSERT_QUERY, genre.getName());
        genre.setGenreId(id);
        return genre;
    }

    public List<Genre> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Genre> findById(long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public List<Genre> findByName(String name) {
        return findMany(FIND_BY_NAME_QUERY, name);
    }

    public Genre update(Genre genre) {
        update(
                UPDATE_QUERY,
                genre.getName(),
                genre.getGenreId()
        );
        return genre;
    }

    public boolean delete(long id) {
        return delete(DELETE_QUERY, id);
    }
}
