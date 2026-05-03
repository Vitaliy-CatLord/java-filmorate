package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.*;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserDbStorage usersStorage;

    public UserDto createUser(NewUserRequest request) {
        User user = UserMapper.mapToUser(request);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return UserMapper.mapToUserDto(usersStorage.save(user));
    }

    public List<UserDto> getAllUsers() {
        return usersStorage.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public UserDto getUserById(Long id) {
        return usersStorage.findById(id)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }


    public UserDto updateUser(UpdateUserRequest request) {
        User user = usersStorage.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + request.getId() + " не найден"));

        UserMapper.updateUserFields(user, request);
        return UserMapper.mapToUserDto(usersStorage.update(user));
    }

    public void addFriend(Long userId, Long friendId) {
        usersStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        usersStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Друг с id " + friendId + " не найден"));

        //первоначально была идея сделать добавление в друзья по схеме Request->Unconfirmed->Confirmed
        //затравка на эту схему есть в usersStorage.requestFriend(userId, friendId);
        //но она вроде как не пройдет автотест, но было бы прикольно
        usersStorage.addFriend(userId, friendId);
        log.info("Пользователь  c ID {} отправил заявку на добавление в друзья ID {}.", userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        usersStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        usersStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Друг с id " + friendId + " не найден"));

        usersStorage.removeFriend(userId, friendId);
        log.info("Пользователь ID {} больше не дружит с ID {}.", userId, friendId);
    }

    public List<UserDto> getUserFriends(Long userId) {
        usersStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        return usersStorage.getFriends(userId).stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    public List<UserDto> getCommonFriends(Long userId, Long friendId) {
        usersStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        usersStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Друг с id " + friendId + " не найден"));

        return usersStorage.getCommonFriends(userId, friendId).stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

}
