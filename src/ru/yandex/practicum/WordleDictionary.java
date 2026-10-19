package ru.yandex.practicum;

import ru.yandex.practicum.exception.game.InvalidWordException;
import ru.yandex.practicum.exception.system.EmptyDictionaryException;

import java.util.List;
import java.util.Random;

/*
этот класс содержит в себе список слов List<String>
    его методы похожи на методы списка, но учитывают особенности игры
    также этот класс может содержать рутинные функции по сравнению слов, букв и т.д.
 */
public class WordleDictionary {

    private final List<String> words;


    public WordleDictionary(List<String> words) {
        this.words = normalizeWords(words);
        if (words.isEmpty())
            throw new EmptyDictionaryException("Словарь не может быть пустым");
    }

    public static String normalize(String word) {
        return word.toLowerCase().replace("ё", "е");
    }

    public boolean contains(String word) {
        return words.contains(normalize(word));
    }

    public static String match(String word, String answer) throws InvalidWordException {
        word = normalize(word);
        if (word.length() != 5)
            throw new InvalidWordException("Слово должно иметь строго 5 букв");

        answer = normalize(answer);

        char[] result = new char[5];
        char[] answerChars = answer.toCharArray();
        boolean[] used = new boolean[5];

        for (int i = 0; i < 5; i++) {
            if (word.charAt(i) == answerChars[i]) {
                result[i] = '+';
                used[i] = true;
            }
        }

        for (int i = 0; i < 5; i++) {
            if (result[i] == '+') continue;

            boolean flag = false;
            for (int j = 0; j < 5; j++) {
                if (!used[j] && word.charAt(i) == answerChars[j]) {
                    result[i] = '^';
                    used[j] = true;
                    flag = true;
                    break;
                }
            }
            if (!flag) result[i] = '-';
        }

        return new String(result);
    }

    public int size() {
        return words.size();
    }

    public boolean isEmpty() {
        return words.isEmpty();
    }

    public String getRandom() {
        if (isEmpty())
            throw new EmptyDictionaryException("На пустом словаре невозможно выбрать слово");
        var random = new Random();
        var index = random.nextInt(size());
        return words.get(index);
    }

    public String get(int index) {
        return words.get(index);
    }

    private List<String> normalizeWords(List<String> words) {
        return words.stream()
                .map(WordleDictionary::normalize)
                .filter(p -> p.length() == 5)
                .toList();
    }

}
