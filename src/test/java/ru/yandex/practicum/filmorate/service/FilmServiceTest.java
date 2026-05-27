package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmServiceTest {

    @Autowired
    private FilmService filmService;

    @Test
    void testDeleteFilmWithWrongIdThrowsNotFoundException() {
        // Проверка: попытка удалить фильм с несуществующим ID выбрасывает NotFoundException
        assertThrows(NotFoundException.class, () -> filmService.deleteFilmById(999L));
    }
}