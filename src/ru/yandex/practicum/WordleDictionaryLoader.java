package ru.yandex.practicum;

import ru.yandex.practicum.exception.system.DictionaryFileNotFoundException;
import ru.yandex.practicum.exception.system.EmptyDictionaryException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/*
этот класс содержит в себе всю рутину по работе с файлами словарей и с кодировками
    ему нужны методы по загрузке списка слов из файла по имени файла
    на выходе должен быть класс WordleDictionary
 */
public class WordleDictionaryLoader {

    public WordleDictionary load(String filename, PrintWriter log) {
        return load(Path.of(filename), log);
    }

    public WordleDictionary load(Path path, PrintWriter log) {
        log.println("Импорт слов из файла " + path.toAbsolutePath());
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank())
                    lines.add(line.trim());
            }
        } catch (IOException exc) {
            throw new DictionaryFileNotFoundException("Не удалось найти словарь " + path.toAbsolutePath());
        }

        log.println("Прочитано " + lines.size() + " строк");

        WordleDictionary dictionary = new WordleDictionary(lines);
        if (dictionary.isEmpty())
            throw new EmptyDictionaryException("Не существует легитимных слов в словаре");

        return dictionary;
    }
}
