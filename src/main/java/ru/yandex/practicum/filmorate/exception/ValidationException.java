package ru.yandex.practicum.filmorate.exception;

import org.slf4j.Logger;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {

        super(message);
    }

    public ValidationException(Logger log, String message) {
        super(message);
        log.error(message);
    }
}
