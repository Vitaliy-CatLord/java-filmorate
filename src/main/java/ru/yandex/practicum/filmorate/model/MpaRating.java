package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MpaRating {

    Integer mpaRatingId;
    String name;

//    G,     // Нет возрастных ограничений
//    PG,    // Рекомендуется присутствие родителей
//    PG_13, // Не желателен для детей до 13 лет
//    R,     // До 17 лет только с взрослым
//    NC_17  // Запрещён для лиц до 18 лет
}
