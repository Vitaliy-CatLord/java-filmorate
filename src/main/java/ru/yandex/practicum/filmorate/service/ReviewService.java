package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmDbStorage;
import ru.yandex.practicum.filmorate.dal.ReviewDbStorage;
import ru.yandex.practicum.filmorate.dal.ReviewRatingDbStorage;
import ru.yandex.practicum.filmorate.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewService {
    ReviewDbStorage reviewStorage;
    ReviewRatingDbStorage ratingStorage;
    UserDbStorage userStorage;
    FilmDbStorage filmStorage;

    public ReviewDto createReview(NewReviewRequest request) {
        if(userStorage.findById(request.getUserId()).isEmpty()) {
            throw new NotFoundException("Пользователь с ID " + request.getUserId() + " не найден");
        }
        if(filmStorage.findById(request.getFilmId()).isEmpty()) {
            throw new NotFoundException("Фильм с ID " + request.getUserId() + " не найден");
        }

        Review review = ReviewMapper.mapToReview(request);

        log.info("Создан новый отзыв с ID: {}", review.getId());
        return ReviewMapper.mapToReviewDto(reviewStorage.save(review));
    }

    public ReviewDto updateReview(UpdateReviewRequest request) {
        if(userStorage.findById(request.getUserId()).isEmpty()) {
            throw new NotFoundException("Пользователь с ID " + request.getUserId() + " не найден");
        }
        if(filmStorage.findById(request.getFilmId()).isEmpty()) {
            throw new NotFoundException("Фильм с ID " + request.getUserId() + " не найден");
        }

        long id = request.getId();
        Review review = reviewStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв с ID " + id + " не найден"));

        ReviewMapper.updateReviewFields(review, request);
        log.info("Отзыв с ID {} обновлён", id);
        return ReviewMapper.mapToReviewDto(reviewStorage.update(review));
    }

    public void deleteReview(Long reviewId) {
        reviewStorage.delete(reviewId);
        log.info("Отзыв с ID {} удалён", reviewId);
    }

    public ReviewDto getReviewById(Long id) {
        Review review = reviewStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв с ID " + id + " не найден"));
        return ReviewMapper.mapToReviewDto(review);
    }

    public List<ReviewDto> getReviews(Long filmId, Integer count) {
        List<Review> reviews = reviewStorage.findByFilmId(filmId, count);
        return reviews.stream()
                .map(ReviewMapper::mapToReviewDto)
                .collect(Collectors.toList());
    }

    public void addLikeReview(Long reviewId, Long userId) {
        ratingStorage.addVote(reviewId, userId, "LIKE");
        log.info("Пользователь {} поставил лайк отзыву {}", userId, reviewId);
    }

    public void addDislikeReview(Long reviewId, Long userId) {
        ratingStorage.addVote(reviewId, userId, "DISLIKE");
        log.info("Пользователь {} поставил дизлайк отзыву {}", userId, reviewId);
    }

    public void removeVote(Long reviewId, Long userId) {
        ratingStorage.removeVote(reviewId, userId);
        log.info("Пользователь {} удалил голос для отзыва {}", userId, reviewId);
    }
}
