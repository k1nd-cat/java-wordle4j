package ru.yandex.practicum.exception.game;

import java.io.Serial;

public class InvalidWordException extends WordleGameException {

    @Serial
    private static final long serialVersionUID = -2453092861521525559L;

    public InvalidWordException(String message) {
        super(message);
    }
}
