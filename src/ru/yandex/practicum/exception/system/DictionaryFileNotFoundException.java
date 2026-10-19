package ru.yandex.practicum.exception.system;

import java.io.Serial;

public class DictionaryFileNotFoundException extends WordleSystemException {

    @Serial
    private static final long serialVersionUID = -6499567449722939083L;

    public DictionaryFileNotFoundException(String message) {
        super(message);
    }
}
