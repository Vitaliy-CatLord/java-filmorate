package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUserRequest {
    Long id;
    String name;
    @Email(message = "Email должен быть в корректной форме")
    @NotBlank(message = "Email не может быть пуст")
    String email;
    @NotBlank(message = "Логин не может быть пуст")
    String login;
    @Past(message = "Дата рождения должна быть в прошлом")
    LocalDate birthday;

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    public boolean hasLogin() {
        return !(login == null || login.isBlank());
    }

    public boolean hasEmail() {
        return !(email == null || email.isBlank());
    }

    public boolean hasBirthday() {
        return birthday != null;
    }

}
