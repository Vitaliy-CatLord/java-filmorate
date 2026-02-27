package ru.yandex.practicum.filmorate.exception;

import org.slf4j.Logger;

public class DuplicatedDataException extends RuntimeException {
    public DuplicatedDataException(String message) {

        super(message);
    }

    public DuplicatedDataException(Logger log, String message) {
        super(message);
        log.error(message);
    }
}
