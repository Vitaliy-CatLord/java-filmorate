package ru.yandex.practicum.filmorate.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventDto {
    Long timestamp;
    Long userId;
    String eventType; // LIKE, REVIEW, FRIEND
    String operation;  // ADD, REMOVE, UPDATE
    Long eventId;
    Long entityId;
}
