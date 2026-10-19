package ru.yandex.practicum.exception.game;

import java.io.Serial;

public class WordNotFoundException extends WordleGameException {

    @Serial
    private static final long serialVersionUID = 1437550697366778243L;

    public WordNotFoundException(String message) {
        super(message);
    }
}
