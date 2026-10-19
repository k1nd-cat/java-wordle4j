package ru.yandex.practicum.exception.system;

import java.io.Serial;

public class LogFileException extends WordleSystemException {

    @Serial
    private static final long serialVersionUID = -8248599141474712628L;

    public LogFileException(String message) {
        super(message);
    }
}
