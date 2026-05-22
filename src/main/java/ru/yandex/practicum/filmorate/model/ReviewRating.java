package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewRating {
    Long reviewId;
    Long userId;
    String ratingType; // "LIKE" или "DISLIKE"
    LocalDateTime ratedAt;
}
