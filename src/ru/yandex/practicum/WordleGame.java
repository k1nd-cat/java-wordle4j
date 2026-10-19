package ru.yandex.practicum;

import ru.yandex.practicum.exception.game.InvalidWordException;
import ru.yandex.practicum.exception.game.WordNotFoundException;
import ru.yandex.practicum.exception.internal.WordleInternalException;
import ru.yandex.practicum.model.Attempt;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
в этом классе хранится словарь и состояние игры
    текущий шаг
    всё что пользователь вводил
    правильный ответ

в этом классе нужны методы, которые
    проанализируют совпадение слова с ответом
    предложат слово-подсказку с учётом всего, что вводил пользователь ранее

не забудьте про специальные типы исключений для игровых и неигровых ошибок
 */
public class WordleGame {

    private final String answer;

    private int steps;

    private final WordleDictionary dictionary;

    private final PrintWriter log;

    private final List<Attempt> attempts;

    private final List<String> usedHints = new ArrayList<>();

    public WordleGame(WordleDictionary dictionary, PrintWriter log) {
        this.dictionary = dictionary;
        this.log = log;
        this.steps = 6;
        this.answer = dictionary.getRandom();
        this.attempts = new ArrayList<>();
    }

    public String makeGuess(String input) throws InvalidWordException, WordNotFoundException {
        input = WordleDictionary.normalize(input);
        if (input.length() != 5)
            throw new InvalidWordException("В веденном слове должно быть 5 букв");

        if (!dictionary.contains(input))
            throw new WordNotFoundException("В словаре не содержится заданного слова");

        steps--;

        String result = WordleDictionary.match(input, answer);
        attempts.add(new Attempt(input, result));

        return result;
    }

    public String getHint() {
        List<String> variants = new ArrayList<>();

        for (int i = 0; i < dictionary.size(); i++) {
            String word = dictionary.get(i);

            if (wasGuessed(word) || usedHints.contains(word)) continue;

            if (fitsAllAttempts(word)) {
                variants.add(word);
            }
        }

        if (variants.isEmpty()) return answer;

        String hint = variants.get(new Random().nextInt(variants.size()));
        usedHints.add(hint);
        log.println("Подсказка: " + hint);
        return hint;
    }

    public boolean isWon(String lastGuess) {
        return answer.equals(lastGuess);
    }

    public boolean isFinished(String lastGuess) {
        return isWon(lastGuess) || steps == 0;
    }

    public String getAnswer() {
        return answer;
    }

    public int getSteps() {
        return steps;
    }

    private boolean wasGuessed(String word) {
        for (var attempt : attempts) {
            if (attempt.word().equals(word)) return true;
        }

        return false;
    }

    private boolean fitsAllAttempts(String candidate) {
        for (Attempt attempt : attempts) {
            try {
                String actual = WordleDictionary.match(attempt.word(), candidate);
                if (!attempt.pattern().equals(actual)) {
                    return false;
                }
            } catch (InvalidWordException e) {
                throw new WordleInternalException();
            }
        }
        return true;
    }
}
