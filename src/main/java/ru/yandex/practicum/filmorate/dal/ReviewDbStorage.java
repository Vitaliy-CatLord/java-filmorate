package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.ReviewRowMapper;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class ReviewDbStorage extends BaseStorage{

    private static final String INSERT_QUERY =
            "INSERT INTO reviews(content, is_positive, user_id, film_id, useful_rating) "
                    + "VALUES (?, ?, ?, ?, 0)";
    private static final String FIND_ALL_QUERY = "SELECT * FROM reviews";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM reviews WHERE id = ?";
    private static final String UPDATE_QUERY =
            "UPDATE reviews SET content = ?, is_positive = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";


    public ReviewDbStorage(JdbcTemplate jdbc, ReviewRowMapper mapper) {
        super(jdbc, mapper);
    }

    public Review save(Review review) {
        Long id = insert(
                INSERT_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId());

        if (id == null) {
            throw new InternalServerException("Не удалось сохранить отзыв — ID не получен");
        }

        review.setId(id);
        return review;
    }

    public List<Review> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Review> findById(long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public List<Review> findByFilmId(Long filmId, Integer count) {
        count = count != null ? count : 10;
        String sql;
        List<Review> reviews;
        if (filmId != null) {
            sql = "SELECT * FROM reviews WHERE film_id = ? " +
                    "ORDER BY useful_rating DESC LIMIT ?";
            reviews = findMany(sql, filmId, count);
        } else {
            sql = "SELECT * FROM reviews ORDER BY useful_rating DESC LIMIT ?";
            reviews = findMany(sql, count);
        }
        return reviews;
    }

    public Review update(Review review) {
        update(
                UPDATE_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getId()
        );
        return review;
    }

    public void delete(Long reviewId) {
        // Удаляем все голоса за отзыв
        String deleteVotesSql = "DELETE FROM review_ratings WHERE review_id = ?";
        delete(deleteVotesSql, reviewId);

        // Удаляем сам отзыв
        String deleteReviewSql = "DELETE FROM reviews WHERE id = ?";
        delete(deleteReviewSql, reviewId);
    }

    // Обновление рейтинга полезности
    public void updateUsefulRating(Long reviewId, int delta) {
        String sql = "UPDATE reviews SET useful_rating = useful_rating + ? WHERE id = ?";
        update(sql, delta, reviewId);
    }



}
