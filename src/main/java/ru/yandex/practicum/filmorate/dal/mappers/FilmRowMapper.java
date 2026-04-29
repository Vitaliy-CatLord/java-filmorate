package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        Timestamp releaseDate = rs.getTimestamp("releaseDate");
        film.setReleaseDate(releaseDate.toLocalDateTime().toLocalDate());
        film.setDuration(rs.getInt("duration"));
        film.setMpaRatingId(rs.getInt("mpaRating_id"));
        return film;
    }
}
