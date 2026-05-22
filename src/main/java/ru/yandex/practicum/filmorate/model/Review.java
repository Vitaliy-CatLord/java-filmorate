package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review {
    @JsonProperty("reviewId")
    Long id;
    String content;
    Boolean isPositive;
    Long userId;
    Long filmId;
    Integer usefulRating = 0;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
