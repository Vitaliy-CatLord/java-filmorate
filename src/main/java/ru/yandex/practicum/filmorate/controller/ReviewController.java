package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@Slf4j
@RestController
@RestControllerAdvice
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private static final String REVIEW_PATH = "/{id}";
    private static final String SETTING_REVIEW_LIKE_PATH = "/{id}/like/{userId}";
    private static final String SETTING_REVIEW_DISLIKE_PATH = "/{id}/dislike/{userId}";

    @PostMapping
    public ReviewDto createNewReview(@RequestBody @Valid NewReviewRequest request) {
        log.info("Выполнение запроса на добавление отзыва {}", request);
        return reviewService.createReview(request);
    }

    @PutMapping
    public ReviewDto updateReview(@RequestBody @Valid UpdateReviewRequest request) {
        log.info("Выполнение запроса на редактирование отзыва {}", request);
        return reviewService.updateReview(request);
    }

    @DeleteMapping(REVIEW_PATH)
    public void deleteReview(@PathVariable Long id) {
        log.info("Выполнение запроса на удаление отзыва с id {}", id);
        reviewService.deleteReview(id);
    }

    @GetMapping(REVIEW_PATH)
    public ReviewDto getReview(@PathVariable Long id) {
        log.info("Выполнение запроса на получение отзыва с id {}", id);
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public List<ReviewDto> getReviews(
            @RequestParam(required = false) Long filmId,
            @RequestParam(defaultValue = "10") Integer count) {
        log.info("Выполнение запроса на получение {} отзывов фильма с id {}", count, filmId);
        return reviewService.getReviews(filmId, count);
    }

    @PutMapping(SETTING_REVIEW_LIKE_PATH)
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Пользователь с id {} поставил лайк отзыву с id {}", userId, id);
        reviewService.addLikeReview(id, userId);
    }

    @PutMapping(SETTING_REVIEW_DISLIKE_PATH)
    public void addDislike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Пользователь с id {} поставил дизлайк отзыву с id {}", userId, id);
        reviewService.addDislikeReview(id, userId);
    }

    @DeleteMapping(SETTING_REVIEW_LIKE_PATH)
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Пользователь с id {} удалил лайк отзыву с id {}", userId, id);
        reviewService.removeVote(id, userId);
    }

    @DeleteMapping(SETTING_REVIEW_DISLIKE_PATH)
    public void removeDislike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Пользователь с id {} удалил дизлайк отзыву с id {}", userId, id);
        reviewService.removeVote(id, userId);
    }

}
