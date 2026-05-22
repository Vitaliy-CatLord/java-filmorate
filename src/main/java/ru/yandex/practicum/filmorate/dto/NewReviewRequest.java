package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewReviewRequest {
    @NotBlank(message = "Описание не может быть пустым")
    private String content;
    @NotNull(message = "Оценка не может быть пустой")
    private Boolean isPositive;
    @NotNull(message = "Идентификатор пользователя не может быть пустой")
    private Long userId;
    @NotNull(message = "Идентификатор фильма не может быть пустой")
    private Long filmId;
}
