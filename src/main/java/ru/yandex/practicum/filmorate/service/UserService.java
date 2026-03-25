package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoudException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage usersStorage;
    private static final LocalDate MIN_TIME_OF_BIRTHDAY = LocalDate.of(1909, 8, 21);

    public UserService(UserStorage usersStorage) {
        this.usersStorage = usersStorage;
    }

    public Collection<User> getAllUsers() {
        return usersStorage.getUsers();
    }

    public User getUserById(Long id) {
        return usersStorage.findById(id)
                .orElseThrow(() -> new NotFoudException("Пользователь с id " + id + " не найден"));
    }


    public User createUser(@RequestBody User newUser) throws ValidationException {
        validateUser(newUser);
        isEmailEmployed(newUser);
        usersStorage.create(newUser);
        log.info("Создан новый юзер {}", newUser);
        return newUser;
    }

    public User updateUser(@RequestBody User newUser) throws ValidationException {
        validateUser(newUser);
        return setOldUser(newUser);
    }

    public void addFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        if (user.equals(friend)) {
            throw new ValidationException("Нельзя добавить в друзья себя");
        }
        user.getFriendsId().add(friendId);
        friend.getFriendsId().add(userId);
        log.info("Пользователь {} теперь друзья с {}.", user.getName(), friend.getName());
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        if (user.equals(friend)) {
            throw new ValidationException("Нельзя удалить из друзья себя");
        }
        user.getFriendsId().remove(friendId);
        friend.getFriendsId().remove(userId);

        log.info("Пользователь {} больше не дружит с {}.", user.getName(), friend.getName());
    }

    public Collection<User> getUserFriends(Long id) {
        User user = getUserById(id);
        return user.getFriendsId().stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        //Set<Long> friendFriendsSet = new HashSet<>(friend.getFriendsId());
        return user.getFriendsId().stream()
                .filter(friend.getFriendsId()::contains)
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    public void cleanStorage() {
        usersStorage.cleanStorage();
    }


    private void validateUser(User newUser) throws ValidationException {
        //mail
        if (newUser.getEmail() == null || newUser.getEmail().isBlank()) {
            String message = "Емеил должен быть заполнен";
            log.warn(message);
            throw new ValidationException(message);
        }
        if (!newUser.getEmail().contains("@")) {
            String message = "Емаил должен содержать @";
            log.warn(message);
            throw new ValidationException(message);
        }
        //login
        if (newUser.getLogin() == null || newUser.getLogin().isBlank()) {
            String message = "Неверно указан логин";
            log.warn(message);
            throw new ValidationException(message);
        }
        if (newUser.getLogin().contains(" ")) {
            String message = "Логин не должен содержать пробелов";
            log.warn(message);
            throw new ValidationException(message);
        }
        //name
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        //birthday
        if (newUser.getBirthday() != null && (newUser.getBirthday().isAfter(LocalDate.now())
                || newUser.getBirthday().isBefore(MIN_TIME_OF_BIRTHDAY))) {
            String message = "Неверно указана дата рождения";
            log.warn(message);
            throw new ValidationException(message);
        }

    }

    private User setOldUser(User newUser) {

        if (newUser.getId() == null) {
            String message = "Идентификатор должен быть указан";
            log.warn(message);
            throw new ValidationException(message);
        }
        //верификация для изменения учетки по совпадению id
        if (usersStorage.findById(newUser.getId()).isPresent()) {
            User oldUser = usersStorage.findById(newUser.getId()).get();
            if (!newUser.getEmail().isBlank()) {
                oldUser.setEmail(newUser.getEmail());
            }
            if (newUser.getName() != null && !newUser.getName().isBlank()) {
                oldUser.setName(newUser.getName());
            }
            if (newUser.getLogin() != null && !newUser.getLogin().isBlank()) {
                oldUser.setLogin(newUser.getLogin());
            }
            if (newUser.getBirthday() != null && newUser.getBirthday().isAfter(MIN_TIME_OF_BIRTHDAY)
                    && newUser.getBirthday().isBefore(LocalDate.now())) {
                oldUser.setBirthday(newUser.getBirthday());
            }
            log.info("Изменен пользователь. Новые данные: {}", oldUser);
            return oldUser;
        } else {
            String message = "Пользователя с Идентификатором " + newUser.getId() + " не существует";
            log.warn(message);
            throw new NotFoudException(message);
        }
    }

    private void isEmailEmployed(User newUser) {
        if (usersStorage.getUsers().stream()
                .anyMatch(oldUser -> newUser.getEmail().equals(oldUser.getEmail()))) {
            String message = "Емеил " + newUser.getEmail() + " занят";
            log.warn(message);
            throw new DuplicatedDataException(message);
        }

    }


}
