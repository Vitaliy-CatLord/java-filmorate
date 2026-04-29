package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FriendshipStatusRowMapper implements RowMapper<FriendshipStatus> {
    @Override
    public FriendshipStatus mapRow(ResultSet rs, int rowNum) throws SQLException {
        FriendshipStatus friendship = new FriendshipStatus();
        friendship.setFriendshipStatusId(rs.getInt("friendshipStatus_id"));
        friendship.setStatusName(rs.getString("statusName"));
        return friendship;
    }
}
