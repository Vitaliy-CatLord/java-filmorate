package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public class UserDbStorage extends BaseStorage<User> {

    private static final String INSERT_QUERY =
            "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM users WHERE email = ?";
    private static final String FIND_BY_LOGIN_QUERY = "SELECT * FROM users WHERE login = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM users WHERE id = ?";

    private static final String ADD_FRIEND_QUERY =
            "INSERT INTO friendList(user_id, friend_id) VALUES (?, ?)";
    private static final String GET_FRIENDS_QUERY = """
            SELECT DISTINCT f1.friend_id
              FROM friendList f1
              WHERE f1.user_id = ?
            """;
    private static final String GET_COMMON_FRIENDS_QUERY = """
            SELECT DISTINCT f1.friend_id
              FROM friendList f1
              JOIN friendList f2 ON f1.friend_id = f2.friend_id
              WHERE f1.user_id = ?
                AND f2.user_id = ?
            """;
    private static final String DELETE_FRIEND_QUERY =
            "DELETE FROM friendList WHERE user_id = ? AND friend_id = ?";

    public UserDbStorage(JdbcTemplate jdbc, UserRowMapper mapper) {
        super(jdbc, mapper);
    }

    public List<User> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<User> findByEmail(String email) {
        return findOne(FIND_BY_EMAIL_QUERY, email);
    }

    public Optional<User> findByLogin(String login) {
        return findOne(FIND_BY_LOGIN_QUERY, login);
    }

    public Optional<User> findById(long userId) {
        return findOne(FIND_BY_ID_QUERY, userId);
    }

    public User save(User user) {
        long id = insert(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    public User update(User user) {
        update(
                UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    public boolean delete(long userId) {
        return delete(DELETE_QUERY, userId);
    }

    public void addFriend(long userId, long friendId) {
        jdbc.update(ADD_FRIEND_QUERY, userId, friendId);
    }

    public List<Long> getFriends(long userId) {
        return jdbc.queryForList(GET_FRIENDS_QUERY, Long.class, userId);
    }

    public List<Long> getCommonFriends(long userId, long anotherId) {
        return jdbc.queryForList(GET_COMMON_FRIENDS_QUERY, Long.class, userId, anotherId);
    }

    public void removeFriend(long userId, long friendId) {
        jdbc.update(DELETE_FRIEND_QUERY, userId, friendId);
    }
}
