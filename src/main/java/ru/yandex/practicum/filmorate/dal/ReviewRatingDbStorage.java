package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.ReviewRatingRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.ReviewRating;

import java.util.Optional;

@Repository
public class ReviewRatingDbStorage extends BaseStorage {
    private final ReviewDbStorage reviewDbStorage;
    public ReviewRatingDbStorage(JdbcTemplate jdbc, ReviewRatingRowMapper mapper, ReviewDbStorage reviewDbStorage) {
        super(jdbc, mapper);
        this.reviewDbStorage = reviewDbStorage;
    }

    public void addVote(Long reviewId, Long userId, String ratingType) {
        if (reviewDbStorage.findById(reviewId).isEmpty()) {
            throw new NotFoundException("Отзыв не найден");
        }
        if (!userExists(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        Optional<ReviewRating> previousVote = getUserVote(reviewId, userId);

        String sql = "MERGE INTO review_ratings (review_id, user_id, rating_type, rated_at)" +
                "KEY (review_id, user_id)" +
                "VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        update(sql, reviewId, userId, ratingType);

        updateReviewUsefulRating(reviewId, previousVote, ratingType);
    }

    public void removeVote(Long reviewId, Long userId) {
        Optional<ReviewRating> removedVote = getUserVote(reviewId, userId);
        String sql = "DELETE FROM review_ratings WHERE review_id = ? AND user_id = ?";
        int rowsAffected = jdbc.update(sql, reviewId, userId);

        if (rowsAffected > 0) {
            // Уменьшаем или увеличиваем рейтинг в зависимости от типа удалённого голоса
            removedVote.ifPresent(vote -> {
                int delta = vote.getRatingType().equals("LIKE") ? -1 : 1;
                reviewDbStorage.updateUsefulRating(reviewId, delta);
            });
        }
    }

    private void updateReviewUsefulRating(Long reviewId, Optional<ReviewRating> previousVote, String newRatingType) {
        int delta = 0;
        if (previousVote.isPresent()) {
            String oldType = previousVote.get().getRatingType();
            if (!oldType.equals(newRatingType)) {
                // Смена голоса: +2 или -2
                delta = newRatingType.equals("LIKE") ? 2 : -2;
            }
        } else {
            // Новый голос: +1 или -1
            delta = newRatingType.equals("LIKE") ? 1 : -1;
        }
        reviewDbStorage.updateUsefulRating(reviewId, delta);
    }

    private Optional<ReviewRating> getUserVote(Long reviewId, Long userId) {
        String sql = "SELECT * FROM review_ratings WHERE review_id = ? AND user_id = ?";
        return findOne(sql, reviewId, userId);
    }

    private boolean userExists(Long userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, userId);
        return count != null && count > 0;
    }

}
