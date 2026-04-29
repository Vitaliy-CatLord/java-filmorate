package ru.yandex.practicum.filmorate.exception;

public class InternalServerExeption extends RuntimeException {
    public InternalServerExeption(String message) {
        super(message);
    }
}
