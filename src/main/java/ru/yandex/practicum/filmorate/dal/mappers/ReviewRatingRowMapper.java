package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.ReviewRating;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReviewRatingRowMapper implements RowMapper<ReviewRating> {
    @Override
    public ReviewRating mapRow (ResultSet rs, int rowNum) throws SQLException {
        ReviewRating reviewRating = new ReviewRating();
        reviewRating.setReviewId(rs.getLong("review_id"));
        reviewRating.setUserId(rs.getLong("user_id"));
        reviewRating.setRatingType(rs.getString("rating_type"));
        reviewRating.setRatedAt(rs.getTimestamp("rated_at").toLocalDateTime());
        return reviewRating;
    }
}
