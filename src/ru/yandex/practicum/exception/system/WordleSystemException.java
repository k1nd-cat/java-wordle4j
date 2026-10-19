package ru.yandex.practicum.exception.system;

import java.io.Serial;

public class WordleSystemException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 8379050728165290984L;

    public WordleSystemException(String message) {
        super(message);
    }
}
