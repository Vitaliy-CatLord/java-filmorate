package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> userStorage = new HashMap<>();

    @Override
    public Collection<User> getUsers() {
        return userStorage.values();
    }

    @Override
    public User create(User user) {
        user.setId(getNextId());
        userStorage.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        return userStorage.put(user.getId(), user);

    }

    @Override
    public User delete(Long id) {
        return userStorage.remove(id);
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(userStorage.get(id));
    }

    private long getNextId() {
        long currentMaxId = userStorage.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public void cleanStorage() {
        userStorage.clear();
    }
}
