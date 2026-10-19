package ru.yandex.practicum.exception.game;

import java.io.Serial;

public class WordleGameException extends Exception {

    @Serial
    private static final long serialVersionUID = 7860312777742520190L;

    public WordleGameException(String message) {
        super(message);
    }
}
