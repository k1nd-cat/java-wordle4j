package ru.yandex.practicum;

import ru.yandex.practicum.exception.game.InvalidWordException;
import ru.yandex.practicum.exception.game.WordNotFoundException;
import ru.yandex.practicum.exception.internal.WordleInternalException;
import ru.yandex.practicum.exception.system.LogFileException;
import ru.yandex.practicum.exception.system.WordleSystemException;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

/*
в главном классе нам нужно:
    создать лог-файл (он должен передаваться во все классы)
    создать загрузчик словарей WordleDictionaryLoader
    загрузить словарь WordleDictionary с помощью класса WordleDictionaryLoader
    затем создать игру WordleGame и передать ей словарь
    вызвать игровой метод в котором в цикле опрашивать пользователя и передавать информацию в игру
    вывести состояние игры и конечный результат
 */
public class Wordle {

    public static void main(String[] args) {
        try (PrintWriter log = createLog();
             Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {

            var loader = new WordleDictionaryLoader();
            var dictionary = loader.load("words_ru.txt", log);
            var game = new WordleGame(dictionary, log);

            while (true) {
                System.out.println("Осталось попыток: " + game.getSteps());
                System.out.println("Введите слово: ");

                if (!scanner.hasNextLine()) {
                    break;
                }

                String input = scanner.nextLine().trim();

                if (input.isEmpty()) {
                    input = game.getHint();
                    System.out.println("Подсказка: " + input);
                }

                log.println("Попытка: " + input);

                try {
                    String result = game.makeGuess(input);
                    String guess = WordleDictionary.normalize(input);

                    System.out.println(result);
                    log.println("Результат: " + result);

                    if (game.isFinished(guess)) {
                        if (game.isWon(guess)) {
                            System.out.println("Победа!");
                            log.println("Победа, ответ: " + game.getAnswer());
                        } else {
                            System.out.println("Поражение, загаданное слово: " + game.getAnswer());
                            log.println("Поражение, ответ: " + game.getAnswer());
                        }
                        break;
                    }
                } catch (InvalidWordException | WordNotFoundException e) {
                    System.err.println(e.getMessage());
                    log.println("Ошибка: " + e.getMessage());
                } catch (WordleSystemException | WordleInternalException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    private static PrintWriter createLog() {
        try {
            return new PrintWriter(Files.newBufferedWriter(Path.of("wordle.log"), StandardCharsets.UTF_8), true);
        } catch (IOException e) {
            throw new LogFileException("Не удалось создать файл для логов");
        }
    }

}
