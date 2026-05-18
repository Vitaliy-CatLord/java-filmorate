package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    long id;
    String email;
    String login;
    String name;
    LocalDate birthday;

}
