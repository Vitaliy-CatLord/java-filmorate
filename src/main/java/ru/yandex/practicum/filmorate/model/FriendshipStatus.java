package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FriendshipStatus {

    Integer friendshipStatusId;
    String statusName;



//    REQUEST,        // отправлен запрос на добавление
//    UNCONFIRMED,    //не просмотрена или отказ
//    CONFIRMED       // в друзьях у ДРУГ ДРУГА
}
