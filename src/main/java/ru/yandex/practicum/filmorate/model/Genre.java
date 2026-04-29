package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Genre {
    Integer genreId;
    String name;


//    COMEDY,       // Комедия
//    DRAMA,        // Драма
//    ANIMATION,    // Мультфильм
//    THRILLER,     // Триллер
//    DOCUMENTARY,  // Документальный
//    ACTION,       // Боевик
//    ROMANCE,      // Мелодрама
//    HORROR,       // Ужасы
//    SCI_FI,       // Научная фантастика
//    FANTASY,      // Фэнтези
//    ADVENTURE,    // Приключения
//    CRIME,        // Криминал
//    MYSTERY,      // Детектив
//    BIOGRAPHY,    // Биография
//    HISTORY,      // Исторический
//    MUSICAL,      // Мюзикл
//    WESTERN,      // Вестерн
//    SPORT,        // Спортивный
//    WAR,          // Военный
//    FAMILY        // Семейный
}
