package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.annotations.AfterMinDate;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewFilmRequest {
    @NotBlank(message = "Название не может быть пустым")
    String name;
    @Size(max = 200, message = "Описание не может быть длиннее 200 символов")
    String description;
    @NotNull
    @PastOrPresent(message = "Дата релиза не может быть в будущем")
    @AfterMinDate
    LocalDate releaseDate;
    @Positive(message = "Продолжительность должна быть положительным числом")
    @NotNull(message = "Продолжительность не может быть пустой")
    Integer duration;

    @JsonProperty("mpa")
    MpaRating mpaRating;
    List<Genre> genres;
}
