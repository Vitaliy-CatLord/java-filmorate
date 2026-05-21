package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void testDeleteUserWithWrongIdThrowsNotFoundException() {
        // Проверка: попытка удалить пользователя с несуществующим ID выбрасывает NotFoundException
        assertThrows(NotFoundException.class, () -> userService.deleteUserById(999L));
    }
}
