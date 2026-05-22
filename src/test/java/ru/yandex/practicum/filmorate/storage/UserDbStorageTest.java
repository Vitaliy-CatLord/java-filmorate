package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dal.UserDbStorage;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {

    private final UserDbStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM friendList");
        jdbcTemplate.update("DELETE FROM likes");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM users");

        jdbcTemplate.update("INSERT INTO users (id, email, login, name, birthday) VALUES (1, '1@m.ru', 'l1', 'n1', '2000-01-01')");
        jdbcTemplate.update("INSERT INTO users (id, email, login, name, birthday) VALUES (2, '2@m.ru', 'l2', 'n2', '2000-01-01')");

        jdbcTemplate.update("INSERT INTO friendList (user_id, friend_id) VALUES (1, 2)");
    }

    @Test
    void testDeleteUserRemovesUserAndFriends() {
        // Проверка: каскадное удаление пользователя по ID очищает список его друзей в БД
        long userIdToDelete = 1L;
        assertTrue(userStorage.findById(userIdToDelete).isPresent());

        boolean isDeleted = userStorage.delete(userIdToDelete);

        assertTrue(isDeleted);
        assertTrue(userStorage.findById(userIdToDelete).isEmpty());
        assertTrue(userStorage.getFriends(2L).isEmpty());
    }
}