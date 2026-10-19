package ru.yandex.practicum.exception.system;

import java.io.Serial;

public class EmptyDictionaryException extends WordleSystemException {

    @Serial
    private static final long serialVersionUID = 6724912266079747342L;

    public EmptyDictionaryException(String message) {
        super(message);
    }
}
