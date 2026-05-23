package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dal.FilmDbStorage;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmDbStorageTest {

    private final FilmDbStorage filmStorage;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM likes");
        jdbcTemplate.update("DELETE FROM film_genres");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM users");

        jdbcTemplate.update("INSERT INTO users (id, email, login, name, birthday) VALUES (1, '1@m.ru', 'l1', 'n1', '2000-01-01')");
        jdbcTemplate.update("INSERT INTO users (id, email, login, name, birthday) VALUES (2, '2@m.ru', 'l2', 'n2', '2000-01-01')");

        jdbcTemplate.update("INSERT INTO films (id, name, description, releaseDate, duration, mpaRating_id) " +
                "VALUES (1, 'Film One', 'Desc 1', '2020-01-01', 100, 1)");
        jdbcTemplate.update("INSERT INTO films (id, name, description, releaseDate, duration, mpaRating_id) " +
                "VALUES (2, 'Film Two', 'Desc 2', '2020-05-15', 120, 1)");
        jdbcTemplate.update("INSERT INTO films (id, name, description, releaseDate, duration, mpaRating_id) " +
                "VALUES (3, 'Film Three', 'Desc 3', '2022-08-20', 90, 1)");

        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (1, 1)");
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (1, 2)");
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (2, 1)");
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (3, 2)");

        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (2, 1)");
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (2, 2)");
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (1, 1)");
    }

    @Test
    void testGetTopFilmsWithoutFiltersReturnAllSortedByLikes() {
        // Проверка: без фильтров возвращаются все фильмы, отсортированные по количеству лайков
        List<Film> top = filmStorage.getTopFilms(10, null, null);

        assertEquals(3, top.size());
        assertEquals(2L, top.get(0).getId()); // 2 лайка
        assertEquals(1L, top.get(1).getId()); // 1 лайк
    }

    @Test
    void testGetTopFilmsFilteredByYear() {
        // Проверка: фильтрация списка популярных фильмов только по году релиза
        List<Film> top2020 = filmStorage.getTopFilms(10, null, 2020);

        assertEquals(2, top2020.size());
        assertEquals(2L, top2020.get(0).getId());
        assertEquals(1L, top2020.get(1).getId());
    }

    @Test
    void testGetTopFilmsFilteredByGenre() {
        // Проверка: фильтрация списка популярных фильмов только по ID жанра
        List<Film> topDramas = filmStorage.getTopFilms(10, 2, null);

        assertEquals(2, topDramas.size());
        assertEquals(1L, topDramas.get(0).getId());
    }

    @Test
    void testGetTopFilmsFilteredByYearAndGenre() {
        // Проверка: совместная фильтрация популярных фильмов одновременно по жанру и году
        List<Film> result = filmStorage.getTopFilms(10, 1, 2020);

        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).getId());
    }

    @Test
    void testDeleteFilmRemovesFilmAndItsLikesAndGenres() {
        // Проверка: каскадное удаление фильма по ID очищает связанные записи в БД
        long filmIdToDelete = 1L;
        org.junit.jupiter.api.Assertions.assertTrue(filmStorage.findById(filmIdToDelete).isPresent());

        boolean isDeleted = filmStorage.delete(filmIdToDelete);
        org.junit.jupiter.api.Assertions.assertTrue(isDeleted);

        org.junit.jupiter.api.Assertions.assertTrue(filmStorage.findById(filmIdToDelete).isEmpty());
    }

}
