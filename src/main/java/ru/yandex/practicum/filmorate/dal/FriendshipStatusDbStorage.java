package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FriendshipStatusRowMapper;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;

import java.util.List;
import java.util.Optional;

@Repository
public class FriendshipStatusDbStorage extends BaseStorage<FriendshipStatus> {
    private static final String INSERT_QUERY = "INSERT INTO friendshipStatus (name) VALUES (?) returning friendshipStatus_id";
    private static final String FIND_ALL_QUERY = "SELECT * FROM friendshipStatus";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM friendshipStatus WHERE id = ?";
    private static final String UPDATE_QUERY = "UPDATE friendshipStatus SET name = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM friendshipStatus WHERE id = ?";

    public FriendshipStatusDbStorage(JdbcTemplate jdbc, FriendshipStatusRowMapper mapper) {
        super(jdbc, mapper);
    }

    public FriendshipStatus save(FriendshipStatus friendshipStatus) {
        Integer id = (int) insert(INSERT_QUERY, friendshipStatus.getStatusName());
        friendshipStatus.setFriendshipStatusId(id);
        return friendshipStatus;
    }

    public List<FriendshipStatus> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<FriendshipStatus> findById (long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public FriendshipStatus update(FriendshipStatus friendshipStatus) {
        update(
                UPDATE_QUERY,
                friendshipStatus.getStatusName()
        );
        return friendshipStatus;
    }

    public boolean delete (long id) {
        return delete(DELETE_QUERY, id);
    }
    
    
}
