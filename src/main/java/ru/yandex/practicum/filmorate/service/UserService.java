package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoudException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.FriendshipStatus;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserService {
    final UserStorage usersStorage;
    static final LocalDate MIN_TIME_OF_BIRTHDAY = LocalDate.of(1909, 8, 21);

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
        FriendshipStatus currentStatus = user.getFriendsList().get(friendId);
        if (currentStatus != null) {
            if (currentStatus.equals(FriendshipStatus.REQUEST)) {
                throw new ValidationException("Заявка на дружбу с " + friend.getName() + " уже отправлена");
            }
            if (currentStatus.equals(FriendshipStatus.CONFIRMED)) {
                throw new ValidationException("Пользователь " + friend.getName() + " уже в друзьях");
            }
        }
        user.getFriendsList().put(friendId, FriendshipStatus.REQUEST);
        friend.getFriendsList().put(userId, FriendshipStatus.UNCONFIRMED);
        log.info("Пользователь {} отправил заявку на добавление в друзья {}.", user.getName(), friend.getName());
    }

    public void confirmFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        if (user.equals(friend)) {
            throw new ValidationException("Нельзя добавить в друзья себя");
        }
        FriendshipStatus status = user.getFriendsList().get(friendId);

        if (status == null) {
            throw new ValidationException("Пользователь " + friend.getName() + " не отправлял вам запрос в друзья");
        }

        if (status.equals(FriendshipStatus.CONFIRMED)) {
            throw new ValidationException("Пользователь " + friend.getName() + " уже в друзьях");
        }

        // Для подтверждения нужна заявка в статусе UNCONFIRMED
        if (!status.equals(FriendshipStatus.UNCONFIRMED)) {
            throw new ValidationException("Невозможно подтвердить дружбу: неверный статус заявки");
        }

        user.getFriendsList().put(friendId, FriendshipStatus.CONFIRMED);
        friend.getFriendsList().put(userId, FriendshipStatus.CONFIRMED);
        log.info("Пользователь {} и {} теперь в друзьях у друг друга.", user.getName(), friend.getName());
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        if (user.equals(friend)) {
            throw new ValidationException("Нельзя удалить из друзья себя");
        }
        user.getFriendsList().remove(friendId);
        friend.getFriendsList().remove(userId);

        log.info("Пользователь {} больше не дружит с {}.", user.getName(), friend.getName());
    }

    public Collection<User> getUserFriends(Long id) {
        return getConfirmedFriendsId(id).stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(Long userId, Long friendId) {
        Set<Long> userFriends = getConfirmedFriendsId(userId);
        Set<Long> friendFriends = getConfirmedFriendsId(friendId);
        userFriends.retainAll(friendFriends);
        return userFriends.stream()
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

    private Set<Long> getConfirmedFriendsId(Long id) {
        User user = getUserById(id);
        return  user.getFriendsList().entrySet().stream()
                .filter(entry -> FriendshipStatus.CONFIRMED.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
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
