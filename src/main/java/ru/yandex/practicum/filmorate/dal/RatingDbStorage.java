package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRatingRowMapper;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;
import java.util.Optional;

@Repository
public class RatingDbStorage extends BaseStorage<MpaRating> {
    private static final String INSERT_QUERY = "INSERT INTO mpaRating (name) VALUES (?)";
    private static final String FIND_ALL_QUERY = "SELECT * FROM mpaRating";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM mpaRating WHERE mpaRating_id = ?";
    private static final String UPDATE_QUERY = "UPDATE mpaRating SET name = ? WHERE mpaRating_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM mpaRating WHERE mpaRating_id = ?";

    public RatingDbStorage(JdbcTemplate jdbc, MpaRatingRowMapper mapper) {
        super(jdbc, mapper);
    }

    public MpaRating save(MpaRating mpaRating) {
        int id = (int) insert(INSERT_QUERY, mpaRating.getName());
        mpaRating.setMpaRatingId(id);
        return mpaRating;
    }

    public List<MpaRating> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<MpaRating> findById(long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public MpaRating update(MpaRating mpaRating) {
        update(
                UPDATE_QUERY,
                mpaRating.getName()
        );
        return mpaRating;
    }

    public boolean delete(long id) {
        return delete(DELETE_QUERY, id);
    }
}
