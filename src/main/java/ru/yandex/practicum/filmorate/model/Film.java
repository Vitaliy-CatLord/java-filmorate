package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.*;

@Data
@EqualsAndHashCode(of = "id")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {

    Long id;
    String name;
    String description;
    LocalDate releaseDate;
    Integer duration;
    Set<Long> likesUserId = new HashSet<>();
    List<Genre> genres = new ArrayList<>();
    MpaRating mpaRating;
    Integer mpaRatingId;
}
