package ru.yandex.practicum.filmorate.dto;


import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.annotations.AfterMinDate;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateFilmRequest {
    @NotNull
    Long id;
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
    Long duration;
    MpaRating mpaRating;
    Set<Genre> genres = new HashSet<>();

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    public boolean hasDescription() {
        return !(description == null || description.isBlank());
    }

    public boolean hasReleaseDate() {
        return releaseDate != null;
    }

    public boolean hasDuration() {
        return duration != null;
    }

    public boolean hasMpa() {
        return mpaRating != null;
    }

    public boolean hasGenres() {
        return genres != null;
    }
}
