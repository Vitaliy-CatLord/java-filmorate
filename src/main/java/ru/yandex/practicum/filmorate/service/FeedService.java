package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.EventDto;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedService {
    //так как лишь добавление события и выдача событий по пользователю,
    // не вижу смысла отдельно создавать слой репозитория
    // и сквозняком тащить все в него сквозь сервис
    JdbcTemplate jdbc;

    public void addEvent(Long userId, String eventType, String operation, Long entityId) {
        String sql = "INSERT INTO user_feeds (user_id, event_type, operation, entity_id, timestamp)" +
                "VALUES (?, ?, ?, ?, ?)";
        long currentTimeMillis = System.currentTimeMillis();
        jdbc.update(sql, userId, eventType, operation, entityId, currentTimeMillis);
    }

    public List<EventDto> getUserFeeds(Long userId) {
        String sql = "SELECT id AS eventId, timestamp, user_id AS userId, " +
                "event_type AS eventType, operation, entity_id AS entityId " +
                "FROM user_feeds " +
                "WHERE user_id = ? ";

        return jdbc.query(sql, (rs, rowNum) -> {
            EventDto event = new EventDto();
            event.setEventId(rs.getLong("eventId"));
            event.setTimestamp(rs.getLong("timestamp"));
            event.setUserId(rs.getLong("userId"));
            event.setEventType(rs.getString("eventType"));
            event.setOperation(rs.getString("operation"));
            event.setEntityId(rs.getLong("entityId"));
            return event;
        }, userId);
    }
}
